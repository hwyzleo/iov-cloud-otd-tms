package net.hwyz.iov.cloud.otd.tms.service.facade.assembler;

import net.hwyz.iov.cloud.otd.tms.api.contract.TransportTypeMpt;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.TransportTypePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 管理后台运输方式转换类
 *
 * @author hwyz_leo
 */
@Mapper
public interface TransportTypeMptAssembler {

    TransportTypeMptAssembler INSTANCE = Mappers.getMapper(TransportTypeMptAssembler.class);

    /**
     * 数据对象转数据传输对象
     *
     * @param transportTypePo 数据对象
     * @return 数据传输对象
     */
    @Mappings({})
    TransportTypeMpt fromPo(TransportTypePo transportTypePo);

    /**
     * 数据传输对象转数据对象
     *
     * @param transportTypeMpt 数据传输对象
     * @return 数据对象
     */
    @Mappings({})
    TransportTypePo toPo(TransportTypeMpt transportTypeMpt);

    /**
     * 数据对象列表转数据传输对象列表
     *
     * @param transportTypePoList 数据对象列表
     * @return 数据传输对象列表
     */
    List<TransportTypeMpt> fromPoList(List<TransportTypePo> transportTypePoList);

}
