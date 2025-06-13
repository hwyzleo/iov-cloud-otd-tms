package net.hwyz.iov.cloud.otd.tms.service.facade.assembler;

import net.hwyz.iov.cloud.otd.tms.api.contract.LogisticsNodeTypeMpt;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LogisticsNodeTypePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 管理后台物流据点类型转换类
 *
 * @author hwyz_leo
 */
@Mapper
public interface LogisticsNodeTypeMptAssembler {

    LogisticsNodeTypeMptAssembler INSTANCE = Mappers.getMapper(LogisticsNodeTypeMptAssembler.class);

    /**
     * 数据对象转数据传输对象
     *
     * @param logisticsNodeTypePo 数据对象
     * @return 数据传输对象
     */
    @Mappings({})
    LogisticsNodeTypeMpt fromPo(LogisticsNodeTypePo logisticsNodeTypePo);

    /**
     * 数据传输对象转数据对象
     *
     * @param logisticsNodeTypeMpt 数据传输对象
     * @return 数据对象
     */
    @Mappings({})
    LogisticsNodeTypePo toPo(LogisticsNodeTypeMpt logisticsNodeTypeMpt);

    /**
     * 数据对象列表转数据传输对象列表
     *
     * @param logisticsNodeTypePoList 数据对象列表
     * @return 数据传输对象列表
     */
    List<LogisticsNodeTypeMpt> fromPoList(List<LogisticsNodeTypePo> logisticsNodeTypePoList);

}
