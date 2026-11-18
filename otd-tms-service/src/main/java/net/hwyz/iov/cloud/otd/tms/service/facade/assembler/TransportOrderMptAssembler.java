package net.hwyz.iov.cloud.otd.tms.service.facade.assembler;

import net.hwyz.iov.cloud.otd.tms.api.contract.TransportOrderDetailMpt;
import net.hwyz.iov.cloud.otd.tms.api.contract.TransportOrderMpt;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrder;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 运输单领域对象 → MPT VO 装配（OTD-TMS-DSN-CR-001）
 *
 * @author hwyz_leo
 */
@Mapper
public interface TransportOrderMptAssembler {

    TransportOrderMptAssembler INSTANCE = Mappers.getMapper(TransportOrderMptAssembler.class);

    /**
     * 领域对象转 VO
     *
     * @param order 运单
     * @return VO
     */
    @Mapping(target = "state", expression = "java(order.getState() == null ? null : order.getState().getCode())")
    @Mapping(target = "stateName", expression = "java(order.getState() == null ? null : order.getState().getDescription())")
    @Mapping(target = "vins", expression = "java(mapVins(order.getDetails()))")
    TransportOrderMpt fromDomain(TransportOrder order);

    /**
     * 批量转换
     *
     * @param orders 运单列表
     * @return VO 列表
     */
    default List<TransportOrderMpt> fromDomainList(List<TransportOrder> orders) {
        return orders.stream().map(this::fromDomain).collect(Collectors.toList());
    }

    /**
     * 提取 VIN 列表
     *
     * @param details 明细
     * @return VIN 列表
     */
    default List<String> mapVins(List<TransportOrderDetail> details) {
        return details == null ? List.of()
                : details.stream().map(TransportOrderDetail::getVin).collect(Collectors.toList());
    }

    /**
     * 明细转 VO
     *
     * @param detail 明细
     * @return VO
     */
    @Mapping(target = "stateName", expression = "java(detailStateName(detail.getState()))")
    TransportOrderDetailMpt fromDetail(TransportOrderDetail detail);

    /**
     * 批量转换明细
     *
     * @param details 明细列表
     * @return VO 列表
     */
    default List<TransportOrderDetailMpt> fromDetailList(List<TransportOrderDetail> details) {
        return details == null ? List.of()
                : details.stream().map(this::fromDetail).collect(Collectors.toList());
    }

    /**
     * WMS 明细状态快照 → 中文名称
     *
     * @param state 状态
     * @return 名称
     */
    default String detailStateName(String state) {
        if (state == null) {
            return null;
        }
        return switch (state) {
            case "RESERVED" -> "已预留";
            case "LOADED" -> "已装车";
            case "OUTBOUND" -> "已出库";
            case "RELEASED" -> "已释放";
            default -> state;
        };
    }
}
