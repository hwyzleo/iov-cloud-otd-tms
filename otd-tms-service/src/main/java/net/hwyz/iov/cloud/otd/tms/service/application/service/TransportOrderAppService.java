package net.hwyz.iov.cloud.otd.tms.service.application.service;

import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.otd.tms.service.application.port.VsoOrderReportPort;
import net.hwyz.iov.cloud.otd.tms.service.common.exception.TmsBusinessException;
import net.hwyz.iov.cloud.otd.tms.service.common.exception.TmsErrorCode;
import net.hwyz.iov.cloud.otd.tms.service.common.util.TmsEventTime;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.InboxRecord;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.OutboxRecord;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrder;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrderDetail;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrderState;
import net.hwyz.iov.cloud.otd.tms.service.domain.repository.TmsInboxRepository;
import net.hwyz.iov.cloud.otd.tms.service.domain.repository.TmsOutboxRepository;
import net.hwyz.iov.cloud.otd.tms.service.domain.repository.TransportOrderRepository;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.config.TmsProperties;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.messaging.TmsEventPayloadSerializer;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.CarrierDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.LogisticsNodeDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.TransportRouteDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.CarrierPo;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodePo;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportRoutePo;
import net.hwyz.iov.cloud.otd.wms.api.contract.event.VehicleOutboundCancellationRequestedEvent;
import net.hwyz.iov.cloud.otd.wms.api.contract.event.VehicleOutboundRequestedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 运单应用服务（OTD-TMS-DSN-CR-001）
 * <p>
 * 职责：受理 VSO 发运申请（幂等建单）、调度并下发出库指令、消费 WMS 出库事实、确认发车/交车/接车、
 * 取消与补偿扫描。状态回写在事务提交后执行（失败不回退运单状态，INV-010）。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransportOrderAppService {

    private static final String SOURCE_SYSTEM_VSO = "VSO";
    private static final String PUBLISH_STATE_PENDING = "PENDING";
    private static final int SCHEMA_VERSION = 2;
    private static final int VSO_PREPARE_TRANSPORT = 500;
    private static final int VSO_TRANSPORTING = 550;
    private static final int VSO_PREPARE_DELIVER = 600;
    private static final String STATE_RESERVED = "RESERVED";
    private static final DateTimeFormatter ORDER_NUM_TIME =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final TransportOrderRepository transportOrderRepository;
    private final TmsOutboxRepository outboxRepository;
    private final TmsInboxRepository inboxRepository;
    private final TmsProperties properties;
    private final TmsEventPayloadSerializer eventPayloadSerializer;
    private final VsoOrderReportPort vsoOrderReportPort;
    private final CarrierDao carrierDao;
    private final TransportRouteDao transportRouteDao;
    private final LogisticsNodeDao logisticsNodeDao;
    private final ZoneId eventZone;

    /**
     * 受理发运申请（VSO → TMS），幂等建单
     *
     * @param eventId      事件 ID
     * @param topic        Topic
     * @param payloadDigest 载荷摘要
     * @param message      发运申请消息
     * @return 运单号
     */
    @Transactional(rollbackFor = Exception.class)
    public String acceptTransportApplied(String eventId, String topic, String payloadDigest,
                                         TransportAppliedMessage message) {
        String businessKey = message.orderNo();
        Optional<InboxRecord> recorded = inboxRepository.find(properties.getConsumer().getTransportAppliedGroup(),
                topic, eventId);
        if (recorded.isPresent()) {
            log.info("发运申请事件[{}]已处理，跳过", eventId);
            return businessKey;
        }
        Optional<TransportOrder> existed = transportOrderRepository.findBySourceOrder(SOURCE_SYSTEM_VSO, businessKey);
        String orderNum;
        if (existed.isPresent()) {
            log.info("订单[{}]已存在运单[{}]，幂等跳过建单", businessKey, existed.get().getOrderNum());
            orderNum = existed.get().getOrderNum();
        } else {
            orderNum = createOrder(message);
        }
        inboxRepository.insert(InboxRecord.builder()
                .consumerName(properties.getConsumer().getTransportAppliedGroup())
                .topic(topic)
                .eventId(eventId)
                .eventType(properties.getTopics().getVsoTransportApplied())
                .payloadDigest(payloadDigest)
                .processState("PROCESSED")
                .businessKey(businessKey)
                .receivedTime(new Date())
                .processedTime(new Date())
                .build());
        return orderNum;
    }

    private String createOrder(TransportAppliedMessage message) {
        if (message.orderNo() == null || message.orderNo().isBlank()) {
            throw new TmsBusinessException(TmsErrorCode.MESSAGE_INVALID, "发运申请缺少订单号");
        }
        if (message.vin() == null || message.vin().isBlank()) {
            throw new TmsBusinessException(TmsErrorCode.MESSAGE_INVALID,
                    "订单[" + message.orderNo() + "]发运申请缺少 VIN");
        }
        if (transportOrderRepository.countActiveByVin(message.vin(), null) > 0) {
            throw new TmsBusinessException(TmsErrorCode.VIN_ALREADY_IN_ORDER,
                    "VIN[" + message.vin() + "]已在其它有效运单中");
        }
        String orderNum = nextOrderNum();
        TransportOrder order = TransportOrder.builder()
                .orderNum(orderNum)
                .sourceSystem(SOURCE_SYSTEM_VSO)
                .sourceOrderNo(message.orderNo())
                .state(TransportOrderState.NOT_DISPATCHED)
                .dispatchVersion(0)
                .details(new ArrayList<>(List.of(TransportOrderDetail.builder()
                        .orderNum(orderNum)
                        .vin(message.vin())
                        .state(STATE_RESERVED)
                        .build())))
                .build();
        transportOrderRepository.save(order);
        log.info("订单[{}]生成运单[{}]，VIN={}", message.orderNo(), orderNum, message.vin());
        return orderNum;
    }

    /**
     * 调度并下发出库指令
     *
     * @param command 调度命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void dispatch(DispatchCommand command) {
        TransportOrder order = loadOrder(command.orderNum());
        validateMasterData(command);
        LogisticsNodePo dispatchNode = logisticsNodeDao.selectPoByCode(command.dispatchLogisticsNodeCode());
        String warehouseCode = dispatchNode == null ? null : dispatchNode.getWarehouseCode();
        String requestId = order.dispatch(command.carrierCode(), command.transportRouteCode(),
                command.dispatchLogisticsNodeCode(), command.handoverLogisticsNodeCode(),
                command.licensePlate(), command.idNumber(), command.estimatedLoadingTime(),
                command.estimatedArrivalTime(), command.dispatcher(), new Date());
        TransportOrder dispatched = withWarehouse(order, warehouseCode);
        transportOrderRepository.save(dispatched);
        appendOutboundCommand(dispatched, requestId);
        reportAfterCommit(VSO_PREPARE_TRANSPORT, dispatched, () -> vsoOrderReportPort.prepareTransport(
                dispatched.getSourceOrderNo()));
    }

    /**
     * 确认发车（全部 VIN 已出库）
     *
     * @param orderNum 运单号
     * @param operator 操作人
     */
    @Transactional(rollbackFor = Exception.class)
    public void depart(String orderNum, String operator) {
        TransportOrder order = loadOrder(orderNum);
        order.depart(new Date());
        transportOrderRepository.save(order);
        reportAfterCommit(VSO_TRANSPORTING, order, () -> vsoOrderReportPort.transporting(order.getSourceOrderNo()));
    }

    /**
     * 交车（承运商交付接车据点）
     *
     * @param orderNum     运单号
     * @param allDelivered 是否全部交车完成
     * @param operator     操作人
     */
    @Transactional(rollbackFor = Exception.class)
    public void handover(String orderNum, boolean allDelivered, String operator) {
        TransportOrder order = loadOrder(orderNum);
        boolean firstHandover = order.getState() != TransportOrderState.HANDED_OVER;
        order.handover(allDelivered, new Date());
        transportOrderRepository.save(order);
        if (allDelivered && firstHandover) {
            reportAfterCommit(VSO_PREPARE_DELIVER, order,
                    () -> vsoOrderReportPort.prepareDelivery(order.getSourceOrderNo()));
        }
    }

    /**
     * 接车（接车方确认接收）
     *
     * @param orderNum    运单号
     * @param allReceived 是否全部接车完成
     * @param operator    操作人
     */
    @Transactional(rollbackFor = Exception.class)
    public void receipt(String orderNum, boolean allReceived, String operator) {
        TransportOrder order = loadOrder(orderNum);
        order.receipt(allReceived, new Date());
        transportOrderRepository.save(order);
    }

    /**
     * 取消运单（仅未装车）
     *
     * @param orderNum 运单号
     * @param reason   取消原因
     * @param operator 操作人
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(String orderNum, String reason, String operator) {
        TransportOrder order = loadOrder(orderNum);
        order.cancel(reason);
        transportOrderRepository.save(order);
        appendCancellation(order, reason);
    }

    /**
     * 手工结束
     *
     * @param orderNum 运单号
     * @param operator 操作人
     */
    @Transactional(rollbackFor = Exception.class)
    public void manualClose(String orderNum, String operator) {
        TransportOrder order = loadOrder(orderNum);
        order.manualClose();
        transportOrderRepository.save(order);
    }

    /**
     * 应用 WMS 出库事实（changed）
     *
     * @param eventId         事件 ID
     * @param topic           Topic
     * @param payloadDigest   载荷摘要
     * @param sourceOrderNum  来源单号（运单号）
     * @param outboundOrderNum WMS 出库单号
     * @param items           明细状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyOutboundFact(String eventId, String topic, String payloadDigest, String sourceOrderNum,
                                  String outboundOrderNum, List<WmsOutboundItem> items) {
        Optional<InboxRecord> recorded = inboxRepository.find(properties.getConsumer().getOutboundFactGroup(),
                topic, eventId);
        if (recorded.isPresent()) {
            log.info("出库事实事件[{}]已处理，跳过", eventId);
            return;
        }
        TransportOrder order = transportOrderRepository.findByOrderNum(sourceOrderNum)
                .orElseThrow(() -> new TmsBusinessException(TmsErrorCode.ORDER_NOT_EXIST,
                        "出库事实对应的运单不存在: " + sourceOrderNum));
        for (WmsOutboundItem item : items) {
            order.applyOutboundFact(item.vin(), item.state(), outboundOrderNum);
        }
        transportOrderRepository.save(order);
        inboxRepository.insert(InboxRecord.builder()
                .consumerName(properties.getConsumer().getOutboundFactGroup())
                .topic(topic)
                .eventId(eventId)
                .eventType(properties.getTopics().getWmsOutboundChanged())
                .payloadDigest(payloadDigest)
                .processState("PROCESSED")
                .businessKey(sourceOrderNum)
                .resultEventId(outboundOrderNum)
                .receivedTime(new Date())
                .processedTime(new Date())
                .build());
    }

    /**
     * 应用 WMS 出库命令拒绝事实
     *
     * @param eventId        事件 ID
     * @param topic          Topic
     * @param payloadDigest  载荷摘要
     * @param sourceOrderNum 来源单号（运单号）
     * @param reasonCode     原因码
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyOutboundRejection(String eventId, String topic, String payloadDigest, String sourceOrderNum,
                                       String reasonCode) {
        Optional<InboxRecord> recorded = inboxRepository.find(properties.getConsumer().getOutboundFactGroup(),
                topic, eventId);
        if (recorded.isPresent()) {
            log.info("出库拒绝事件[{}]已处理，跳过", eventId);
            return;
        }
        TransportOrder order = transportOrderRepository.findByOrderNum(sourceOrderNum)
                .orElseThrow(() -> new TmsBusinessException(TmsErrorCode.ORDER_NOT_EXIST,
                        "出库拒绝事实对应的运单不存在: " + sourceOrderNum));
        order.markRejected(reasonCode);
        transportOrderRepository.save(order);
        inboxRepository.insert(InboxRecord.builder()
                .consumerName(properties.getConsumer().getOutboundFactGroup())
                .topic(topic)
                .eventId(eventId)
                .eventType(properties.getTopics().getWmsOutboundCommandRejected())
                .payloadDigest(payloadDigest)
                .processState("BUSINESS_REJECTED")
                .businessKey(sourceOrderNum)
                .errorCode(reasonCode)
                .receivedTime(new Date())
                .processedTime(new Date())
                .build());
        log.warn("运单[{}]出库指令被拒绝，原因码={}", sourceOrderNum, reasonCode);
    }

    /**
     * 补偿：为 VSO 待发运订单补齐运单
     *
     * @param order VSO 待发运订单
     * @return 是否新建
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean compensate(VsoOrderReportPort.VsoTransportAppliedOrder order) {
        if (transportOrderRepository.findBySourceOrder(SOURCE_SYSTEM_VSO, order.orderNo()).isPresent()) {
            return false;
        }
        createOrder(new TransportAppliedMessage(order.orderNo(), order.vin(), order.deliveryStoreCode(),
                order.deliveryRegionCode()));
        return true;
    }

    /**
     * 查询运单
     *
     * @param orderNum 运单号
     * @return 运单
     */
    public TransportOrder getByOrderNum(String orderNum) {
        return loadOrder(orderNum);
    }

    /**
     * 运单列表（配合 PageHelper 数据库分页）
     *
     * @param orderNum  运单号
     * @param state     状态
     * @param carrier   承运商
     * @param nodeCode  据点
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 运单列表
     */
    public List<TransportOrder> search(String orderNum, TransportOrderState state, String carrier,
                                       String nodeCode, Date beginTime, Date endTime) {
        return transportOrderRepository.search(orderNum, state, carrier, nodeCode, beginTime, endTime);
    }

    private TransportOrder loadOrder(String orderNum) {
        return transportOrderRepository.findByOrderNum(orderNum)
                .orElseThrow(() -> new TmsBusinessException(TmsErrorCode.ORDER_NOT_EXIST, "运单不存在: " + orderNum));
    }

    private void validateMasterData(DispatchCommand command) {
        CarrierPo carrier = carrierDao.selectPoByCode(command.carrierCode());
        if (carrier == null || !Boolean.TRUE.equals(carrier.getEnable())) {
            throw new TmsBusinessException(TmsErrorCode.MASTER_DATA_UNAVAILABLE,
                    "承运商[" + command.carrierCode() + "]不可用");
        }
        TransportRoutePo route = transportRouteDao.selectPoByCode(command.transportRouteCode());
        if (route == null || !Boolean.TRUE.equals(route.getEnable())) {
            throw new TmsBusinessException(TmsErrorCode.MASTER_DATA_UNAVAILABLE,
                    "运输线路[" + command.transportRouteCode() + "]不可用");
        }
        if (route.getCarrierCode() != null && !route.getCarrierCode().equals(command.carrierCode())) {
            throw new TmsBusinessException(TmsErrorCode.MASTER_DATA_UNAVAILABLE,
                    "运输线路[" + route.getCode() + "]不属于承运商[" + command.carrierCode() + "]");
        }
        requireNode(command.dispatchLogisticsNodeCode(), "发车据点");
        requireNode(command.handoverLogisticsNodeCode(), "接车据点");
    }

    private void requireNode(String code, String label) {
        LogisticsNodePo node = logisticsNodeDao.selectPoByCode(code);
        if (node == null || !Boolean.TRUE.equals(node.getEnable())) {
            throw new TmsBusinessException(TmsErrorCode.MASTER_DATA_UNAVAILABLE, label + "[" + code + "]不可用");
        }
    }

    private TransportOrder withWarehouse(TransportOrder order, String warehouseCode) {
        return TransportOrder.builder()
                .id(order.getId())
                .orderNum(order.getOrderNum())
                .sourceSystem(order.getSourceSystem())
                .sourceOrderNo(order.getSourceOrderNo())
                .warehouseCode(warehouseCode)
                .carrierCode(order.getCarrierCode())
                .transportRouteCode(order.getTransportRouteCode())
                .dispatchLogisticsNodeCode(order.getDispatchLogisticsNodeCode())
                .handoverLogisticsNodeCode(order.getHandoverLogisticsNodeCode())
                .state(order.getState())
                .estimatedLoadingTime(order.getEstimatedLoadingTime())
                .estimatedArrivalTime(order.getEstimatedArrivalTime())
                .dispatcher(order.getDispatcher())
                .dispatchTime(order.getDispatchTime())
                .confirm(order.getConfirm())
                .dispatchVersion(order.getDispatchVersion())
                .rejectReason(order.getRejectReason())
                .lastReportedVsoState(order.getLastReportedVsoState())
                .details(order.getDetails())
                .build();
    }

    private void appendOutboundCommand(TransportOrder order, String requestId) {
        Instant now = TmsEventTime.now();
        VehicleOutboundRequestedEvent event = VehicleOutboundRequestedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("VehicleOutboundRequested")
                .schemaVersion(SCHEMA_VERSION)
                .occurredAt(TmsEventTime.format(now, eventZone))
                .producer("otd-tms")
                .payload(VehicleOutboundRequestedEvent.Payload.builder()
                        .requestId(requestId)
                        .sourceSystem(properties.getSourceSystem())
                        .sourceOrderNum(order.getOrderNum())
                        .warehouseCode(order.getWarehouseCode())
                        .vins(order.getDetails().stream().map(TransportOrderDetail::getVin).toList())
                        .build())
                .build();
        outboxRepository.insert(OutboxRecord.builder()
                .eventId(event.getEventId())
                .eventType("VehicleOutboundRequested")
                .aggregateType(properties.getOutbox().getAggregateType())
                .aggregateId(order.getOrderNum())
                .aggregateVersion((long) order.getDispatchVersion())
                .topic(properties.getTopics().getWmsOutboundRequested())
                .messageKey(order.getOrderNum())
                .payload(eventPayloadSerializer.serialize(event))
                .publishState(PUBLISH_STATE_PENDING)
                .retryCount(0)
                .build());
    }

    private void appendCancellation(TransportOrder order, String reason) {
        Instant now = TmsEventTime.now();
        VehicleOutboundCancellationRequestedEvent event = VehicleOutboundCancellationRequestedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("VehicleOutboundCancellationRequested")
                .schemaVersion(SCHEMA_VERSION)
                .occurredAt(TmsEventTime.format(now, eventZone))
                .producer("otd-tms")
                .payload(VehicleOutboundCancellationRequestedEvent.Payload.builder()
                        .requestId(order.requestId())
                        .sourceSystem(properties.getSourceSystem())
                        .sourceOrderNum(order.getOrderNum())
                        .reason(reason)
                        .build())
                .build();
        outboxRepository.insert(OutboxRecord.builder()
                .eventId(event.getEventId())
                .eventType("VehicleOutboundCancellationRequested")
                .aggregateType(properties.getOutbox().getAggregateType())
                .aggregateId(order.getOrderNum())
                .aggregateVersion((long) (order.getDispatchVersion() == null ? 0 : order.getDispatchVersion()))
                .topic(properties.getTopics().getWmsOutboundCancellationRequested())
                .messageKey(order.getOrderNum())
                .payload(eventPayloadSerializer.serialize(event))
                .publishState(PUBLISH_STATE_PENDING)
                .retryCount(0)
                .build());
    }

    private void reportAfterCommit(int vsoState, TransportOrder order, Runnable report) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            reportSafely(vsoState, order, report);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                reportSafely(vsoState, order, report);
            }
        });
    }

    private void reportSafely(int vsoState, TransportOrder order, Runnable report) {
        try {
            report.run();
            transportOrderRepository.findByOrderNum(order.getOrderNum()).ifPresent(current -> {
                current.markVsoReported(vsoState);
                transportOrderRepository.save(current);
            });
        } catch (Exception e) {
            // INV-010：回写失败不回退运单状态，记录并交由对账/人工处置
            log.error("运单[{}]回写 VSO 状态[{}]失败: {}", order.getOrderNum(), vsoState, e.getMessage(), e);
        }
    }

    private String nextOrderNum() {
        return "TO" + LocalDateTime.now(eventZone).format(ORDER_NUM_TIME) + RandomUtil.randomNumbers(3);
    }

    /**
     * 发运申请消息（VSO → TMS）
     *
     * @param orderNo           订单号
     * @param vin               交付 VIN
     * @param deliveryStoreCode 交付门店
     * @param deliveryRegionCode 交付区域
     */
    public record TransportAppliedMessage(String orderNo, String vin, String deliveryStoreCode,
                                          String deliveryRegionCode) {
    }

    /**
     * 调度命令
     *
     * @param orderNum                  运单号
     * @param carrierCode               承运商
     * @param transportRouteCode        线路
     * @param dispatchLogisticsNodeCode 发车据点
     * @param handoverLogisticsNodeCode 接车据点
     * @param licensePlate              运输车辆
     * @param idNumber                  驾驶员身份证号
     * @param estimatedLoadingTime      预计装车时间
     * @param estimatedArrivalTime      预计到达时间
     * @param dispatcher                调度人员
     */
    public record DispatchCommand(String orderNum, String carrierCode, String transportRouteCode,
                                  String dispatchLogisticsNodeCode, String handoverLogisticsNodeCode,
                                  String licensePlate, String idNumber, Date estimatedLoadingTime,
                                  Date estimatedArrivalTime, String dispatcher) {
    }

    /**
     * WMS 出库明细事实
     *
     * @param vin   车架号
     * @param state 明细状态
     */
    public record WmsOutboundItem(String vin, String state) {
    }
}
