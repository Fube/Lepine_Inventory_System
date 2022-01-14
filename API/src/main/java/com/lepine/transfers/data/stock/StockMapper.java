package com.lepine.transfers.data.stock;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mappings({
            @Mapping(target = "uuid", ignore = true),
    })
    Stock toEntity(StockUUIDLessDTO stockUUIDLessDTO);
}
