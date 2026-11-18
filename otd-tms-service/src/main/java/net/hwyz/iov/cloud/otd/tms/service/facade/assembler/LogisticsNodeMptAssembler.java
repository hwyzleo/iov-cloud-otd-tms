package net.hwyz.iov.cloud.otd.tms.service.facade.assembler;

import net.hwyz.iov.cloud.otd.tms.api.contract.LogisticsNodeMpt;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 管理后台物流据点信息转换类
 *
 * @author hwyz_leo
 */
@Mapper
public interface LogisticsNodeMptAssembler {

    LogisticsNodeMptAssembler INSTANCE = Mappers.getMapper(LogisticsNodeMptAssembler.class);

    /**
     * 数据对象转数据传输对象
     *
     * @param logisticsNodePo 数据对象
     * @return 数据传输对象
     */
    @Mappings({})
    LogisticsNodeMpt fromPo(LogisticsNodePo logisticsNodePo);

    /**
     * 数据传输对象转数据对象
     *
     * @param logisticsNodeMpt 数据传输对象
     * @return 数据对象
     */
    @Mappings({})
    LogisticsNodePo toPo(LogisticsNodeMpt logisticsNodeMpt);

    /**
     * 数据对象列表转数据传输对象列表
     *
     * @param logisticsNodePoList 数据对象列表
     * @return 数据传输对象列表
     */
    List<LogisticsNodeMpt> fromPoList(List<LogisticsNodePo> logisticsNodePoList);

}
