package net.hwyz.iov.cloud.otd.tms.service.facade.assembler;

import net.hwyz.iov.cloud.otd.tms.api.contract.LoadingVehicleMpt;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.LoadingVehiclePo;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 管理后台装载车转换类
 *
 * @author hwyz_leo
 */
@Mapper
public interface LoadingVehicleMptAssembler {

    LoadingVehicleMptAssembler INSTANCE = Mappers.getMapper(LoadingVehicleMptAssembler.class);

    /**
     * 数据对象转数据传输对象
     *
     * @param loadingVehiclePo 数据对象
     * @return 数据传输对象
     */
    @Mappings({})
    LoadingVehicleMpt fromPo(LoadingVehiclePo loadingVehiclePo);

    /**
     * 数据传输对象转数据对象
     *
     * @param loadingVehicleMpt 数据传输对象
     * @return 数据对象
     */
    @Mappings({})
    LoadingVehiclePo toPo(LoadingVehicleMpt loadingVehicleMpt);

    /**
     * 数据对象列表转数据传输对象列表
     *
     * @param loadingVehiclePoList 数据对象列表
     * @return 数据传输对象列表
     */
    List<LoadingVehicleMpt> fromPoList(List<LoadingVehiclePo> loadingVehiclePoList);

}
