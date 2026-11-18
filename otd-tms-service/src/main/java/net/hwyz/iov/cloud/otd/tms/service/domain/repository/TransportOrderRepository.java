package net.hwyz.iov.cloud.otd.tms.service.domain.repository;

import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrder;
import net.hwyz.iov.cloud.otd.tms.service.domain.model.TransportOrderState;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 运输单仓储（领域定义的持久化端口，OTD-TMS-DSN-CR-001）
 *
 * @author hwyz_leo
 */
public interface TransportOrderRepository {

    /**
     * 按运单号查询（含明细）
     *
     * @param orderNum 运单号
     * @return 运单
     */
    Optional<TransportOrder> findByOrderNum(String orderNum);

    /**
     * 按来源订单查询
     *
     * @param sourceSystem  来源系统
     * @param sourceOrderNo 来源订单号
     * @return 运单
     */
    Optional<TransportOrder> findBySourceOrder(String sourceSystem, String sourceOrderNo);

    /**
     * 按状态与创建时间查询（补偿扫描用）
     *
     * @param states 状态集合
     * @param limit  条数上限
     * @return 运单号列表（不含明细）
     */
    List<String> findOrderNumsByStates(List<TransportOrderState> states, int limit);

    /**
     * 分页查询（配合 PageHelper，数据库分页）
     *
     * @param orderNum  运单号（可空）
     * @param state     状态（可空）
     * @param carrier   承运商代码（可空）
     * @param nodeCode  据点代码（可空，发车或接车据点命中）
     * @param beginTime 创建开始时间（可空）
     * @param endTime   创建结束时间（可空）
     * @return 运单列表（不含明细）
     */
    List<TransportOrder> search(String orderNum, TransportOrderState state, String carrier,
                                String nodeCode, Date beginTime, Date endTime);

    /**
     * 保存运单（新建或更新聚合与明细）
     *
     * @param order 运单
     */
    void save(TransportOrder order);

    /**
     * 统计 VIN 在其它有效运单中的数量
     *
     * @param vin      车架号
     * @param orderNum 排除的运单号（可空）
     * @return 数量
     */
    int countActiveByVin(String vin, String orderNum);
}
