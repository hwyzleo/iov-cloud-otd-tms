package net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrder;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrderDetail;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrderState;
import net.hwyz.iov.cloud.otd.tms.service.domain.repository.TransportOrderRepository;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.TransportOrderDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.dao.TransportOrderDetailDao;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportOrderDetailPo;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportOrderPo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 运输单仓储实现（OTD-TMS-DSN-CR-001）
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class TransportOrderRepositoryImpl implements TransportOrderRepository {

    private final TransportOrderDao transportOrderDao;
    private final TransportOrderDetailDao transportOrderDetailDao;

    @Override
    public Optional<TransportOrder> findByOrderNum(String orderNum) {
        TransportOrderPo po = transportOrderDao.selectPoByOrderNum(orderNum);
        return po == null ? Optional.empty() : Optional.of(toDomain(po, true));
    }

    @Override
    public Optional<TransportOrder> findBySourceOrder(String sourceSystem, String sourceOrderNo) {
        TransportOrderPo po = transportOrderDao.selectPoBySourceOrder(sourceSystem, sourceOrderNo);
        return po == null ? Optional.empty() : Optional.of(toDomain(po, true));
    }

    @Override
    public List<String> findOrderNumsByStates(List<TransportOrderState> states, int limit) {
        if (states == null || states.isEmpty()) {
            return List.of();
        }
        List<Integer> codes = states.stream().map(TransportOrderState::getCode).collect(Collectors.toList());
        return transportOrderDao.selectOrderNumsByStates(codes, limit);
    }

    @Override
    public List<TransportOrder> search(String orderNum, TransportOrderState state, String carrier,
                                       String nodeCode, Date beginTime, Date endTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderNum", orderNum);
        params.put("state", state == null ? null : state.getCode());
        params.put("carrierCode", carrier);
        params.put("nodeCode", nodeCode);
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        return transportOrderDao.selectPoByMap(params).stream()
                .map(po -> toDomain(po, false))
                .collect(Collectors.toList());
    }

    @Override
    public void save(TransportOrder order) {
        TransportOrderPo po = toPo(order);
        if (po.getId() == null) {
            transportOrderDao.insertPo(po);
            final TransportOrder saved = withId(order, po.getId());
            if (!saved.getDetails().isEmpty()) {
                transportOrderDetailDao.batchInsertPo(saved.getDetails().stream()
                        .map(detail -> toDetailPo(saved.getOrderNum(), detail))
                        .collect(Collectors.toList()));
            }
            return;
        }
        transportOrderDao.updatePo(po);
        for (TransportOrderDetail detail : order.getDetails()) {
            TransportOrderDetailPo detailPo = toDetailPo(order.getOrderNum(), detail);
            if (detailPo.getId() == null) {
                transportOrderDetailDao.batchInsertPo(List.of(detailPo));
            } else {
                transportOrderDetailDao.updatePo(detailPo);
            }
        }
    }

    @Override
    public int countActiveByVin(String vin, String orderNum) {
        return transportOrderDao.countActiveByVin(vin, orderNum);
    }

    private TransportOrder withId(TransportOrder order, Long id) {
        return TransportOrder.builder()
                .id(id)
                .orderNum(order.getOrderNum())
                .sourceSystem(order.getSourceSystem())
                .sourceOrderNo(order.getSourceOrderNo())
                .warehouseCode(order.getWarehouseCode())
                .carrierCode(order.getCarrierCode())
                .transportRouteCode(order.getTransportRouteCode())
                .dispatchLogisticsNodeCode(order.getDispatchLogisticsNodeCode())
                .handoverLogisticsNodeCode(order.getHandoverLogisticsNodeCode())
                .state(order.getState())
                .estimatedLoadingTime(order.getEstimatedLoadingTime())
                .estimatedArrivalTime(order.getEstimatedArrivalTime())
                .vehicleDispatchTime(order.getVehicleDispatchTime())
                .deliveryTime(order.getDeliveryTime())
                .receiptTime(order.getReceiptTime())
                .licensePlate(order.getLicensePlate())
                .idNumber(order.getIdNumber())
                .dispatcher(order.getDispatcher())
                .dispatchTime(order.getDispatchTime())
                .confirm(order.getConfirm())
                .dispatchVersion(order.getDispatchVersion())
                .rejectReason(order.getRejectReason())
                .lastReportedVsoState(order.getLastReportedVsoState())
                .inboundRequestSeq(order.getInboundRequestSeq())
                .details(order.getDetails())
                .build();
    }

    private TransportOrder toDomain(TransportOrderPo po, boolean withDetails) {
        return TransportOrder.builder()
                .id(po.getId())
                .orderNum(po.getOrderNum())
                .sourceSystem(po.getSourceSystem())
                .sourceOrderNo(po.getSourceOrderNo())
                .warehouseCode(po.getWarehouseCode())
                .carrierCode(po.getCarrierCode())
                .transportRouteCode(po.getTransportRouteCode())
                .dispatchLogisticsNodeCode(po.getDispatchLogisticsNodeCode())
                .handoverLogisticsNodeCode(po.getHandoverLogisticsNodeCode())
                .state(TransportOrderState.fromCode(po.getState()))
                .estimatedLoadingTime(po.getEstimatedLoadingTime())
                .estimatedArrivalTime(po.getEstimatedArrivalTime())
                .vehicleDispatchTime(po.getVehicleDispatchTime())
                .deliveryTime(po.getDeliveryTime())
                .receiptTime(po.getReceiptTime())
                .licensePlate(po.getLicensePlate())
                .idNumber(po.getIdNumber())
                .dispatcher(po.getDispatcher())
                .dispatchTime(po.getDispatchTime())
                .confirm(po.getConfirm())
                .dispatchVersion(po.getDispatchVersion())
                .rejectReason(po.getRejectReason())
                .lastReportedVsoState(po.getLastReportedVsoState())
                .inboundRequestSeq(po.getInboundRequestSeq())
                .details(withDetails ? loadDetails(po.getOrderNum()) : new java.util.ArrayList<>())
                .build();
    }

    private List<TransportOrderDetail> loadDetails(String orderNum) {
        return transportOrderDetailDao.selectPoByOrderNum(orderNum).stream()
                .map(po -> TransportOrderDetail.builder()
                        .id(po.getId())
                        .orderNum(po.getOrderNum())
                        .vin(po.getVin())
                        .state(po.getState())
                        .outboundOrderNum(po.getOutboundOrderNum())
                        .loadingLine(po.getLoadingLine())
                        .deliveryTime(po.getDeliveryTime())
                        .lock(po.getLock())
                        .build())
                .collect(Collectors.toList());
    }

    private TransportOrderPo toPo(TransportOrder order) {
        return TransportOrderPo.builder()
                .id(order.getId())
                .orderNum(order.getOrderNum())
                .sourceSystem(order.getSourceSystem())
                .sourceOrderNo(order.getSourceOrderNo())
                .warehouseCode(order.getWarehouseCode())
                .carrierCode(order.getCarrierCode())
                .state(order.getState() == null ? null : order.getState().getCode())
                .transportRouteCode(order.getTransportRouteCode())
                .dispatchLogisticsNodeCode(order.getDispatchLogisticsNodeCode())
                .estimatedLoadingTime(order.getEstimatedLoadingTime())
                .estimatedArrivalTime(order.getEstimatedArrivalTime())
                .vehicleDispatchTime(order.getVehicleDispatchTime())
                .handoverLogisticsNodeCode(order.getHandoverLogisticsNodeCode())
                .deliveryTime(order.getDeliveryTime())
                .receiptTime(order.getReceiptTime())
                .licensePlate(order.getLicensePlate())
                .idNumber(order.getIdNumber())
                .dispatcher(order.getDispatcher())
                .dispatchTime(order.getDispatchTime())
                .confirm(order.getConfirm())
                .dispatchVersion(order.getDispatchVersion())
                .rejectReason(order.getRejectReason())
                .lastReportedVsoState(order.getLastReportedVsoState())
                .inboundRequestSeq(order.getInboundRequestSeq())
                .build();
    }

    private TransportOrderDetailPo toDetailPo(String orderNum, TransportOrderDetail detail) {
        return TransportOrderDetailPo.builder()
                .id(detail.getId())
                .orderNum(orderNum)
                .vin(detail.getVin())
                .state(detail.getState())
                .outboundOrderNum(detail.getOutboundOrderNum())
                .loadingLine(detail.getLoadingLine())
                .deliveryTime(detail.getDeliveryTime())
                .lock(detail.getLock())
                .build();
    }
}
