package net.hwyz.iov.cloud.otd.tms.service.facade.assembler;

import net.hwyz.iov.cloud.otd.tms.api.contract.CarrierMpt;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.CarrierPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 管理后台承运商转换类
 *
 * @author hwyz_leo
 */
@Mapper
public interface CarrierMptAssembler {

    CarrierMptAssembler INSTANCE = Mappers.getMapper(CarrierMptAssembler.class);

    /**
     * 数据对象转数据传输对象
     *
     * @param carrierPo 数据对象
     * @return 数据传输对象
     */
    @Mappings({})
    CarrierMpt fromPo(CarrierPo carrierPo);

    /**
     * 数据传输对象转数据对象
     *
     * @param carrierMpt 数据传输对象
     * @return 数据对象
     */
    @Mappings({})
    CarrierPo toPo(CarrierMpt carrierMpt);

    /**
     * 数据对象列表转数据传输对象列表
     *
     * @param carrierPoList 数据对象列表
     * @return 数据传输对象列表
     */
    List<CarrierMpt> fromPoList(List<CarrierPo> carrierPoList);

}
