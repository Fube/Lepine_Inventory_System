package com.lepine.transfers.data.warehouse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mappings({
            @Mapping(target = "uuid", ignore = true),
            @Mapping(target = "active", ignore = true),
    })
    Warehouse toEntity(WarehouseActiveLessUUIDLessDTO warehouseActiveLessUUIDLessDTO);

    @Mappings({
            @Mapping(target = "uuid", ignore = true),
    })
    Warehouse toEntity(WarehouseUUIDLessDTO warehouseUUIDLessDTO);
}
