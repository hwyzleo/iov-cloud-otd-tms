package net.hwyz.iov.cloud.otd.tms.service.facade.assembler;

import net.hwyz.iov.cloud.otd.tms.api.contract.TransportRouteMpt;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportRoutePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 管理后台运输线路转换类
 *
 * @author hwyz_leo
 */
@Mapper
public interface TransportRouteMptAssembler {

    TransportRouteMptAssembler INSTANCE = Mappers.getMapper(TransportRouteMptAssembler.class);

    /**
     * 数据对象转数据传输对象
     *
     * @param transportRoutePo 数据对象
     * @return 数据传输对象
     */
    @Mappings({})
    TransportRouteMpt fromPo(TransportRoutePo transportRoutePo);

    /**
     * 数据传输对象转数据对象
     *
     * @param transportRouteMpt 数据传输对象
     * @return 数据对象
     */
    @Mappings({})
    TransportRoutePo toPo(TransportRouteMpt transportRouteMpt);

    /**
     * 数据对象列表转数据传输对象列表
     *
     * @param transportRoutePoList 数据对象列表
     * @return 数据传输对象列表
     */
    List<TransportRouteMpt> fromPoList(List<TransportRoutePo> transportRoutePoList);

}
