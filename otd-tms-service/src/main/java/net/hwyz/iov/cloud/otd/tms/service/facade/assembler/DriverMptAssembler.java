package net.hwyz.iov.cloud.otd.tms.service.facade.assembler;

import net.hwyz.iov.cloud.otd.tms.api.contract.DriverMpt;
import net.hwyz.iov.cloud.otd.tms.service.infrastructure.repository.po.DriverPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 管理后台驾驶员转换类
 *
 * @author hwyz_leo
 */
@Mapper
public interface DriverMptAssembler {

    DriverMptAssembler INSTANCE = Mappers.getMapper(DriverMptAssembler.class);

    /**
     * 数据对象转数据传输对象
     *
     * @param driverPo 数据对象
     * @return 数据传输对象
     */
    @Mappings({})
    DriverMpt fromPo(DriverPo driverPo);

    /**
     * 数据传输对象转数据对象
     *
     * @param driverMpt 数据传输对象
     * @return 数据对象
     */
    @Mappings({})
    DriverPo toPo(DriverMpt driverMpt);

    /**
     * 数据对象列表转数据传输对象列表
     *
     * @param driverPoList 数据对象列表
     * @return 数据传输对象列表
     */
    List<DriverMpt> fromPoList(List<DriverPo> driverPoList);

}
