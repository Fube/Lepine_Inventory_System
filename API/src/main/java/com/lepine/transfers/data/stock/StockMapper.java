package com.lepine.transfers.data.stock;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mappings({
            @Mapping(target = "uuid", ignore = true),
            @Mapping(target = "item.uuid", source = "itemUuid"),
            @Mapping(target = "warehouse.uuid", source = "warehouseUuid"),
    })
    Stock toEntity(StockUuidLessItemUuidWarehouseUuid stockDTO);

    @Mappings({
            @Mapping(target = "objectID", source = "uuid"),

            @Mapping(target = "itemUuid", source = "item.uuid"),
            @Mapping(target = "name", source = "item.name"),
            @Mapping(target = "sku", source = "item.sku"),
            @Mapping(target = "description", source = "item.description"),

            @Mapping(target = "warehouseUuid", source = "warehouse.uuid"),
            @Mapping(target = "zipCode", source = "warehouse.zipCode"),
    })
    StockSearchDTO toSearchDTO(Stock stock);
}
