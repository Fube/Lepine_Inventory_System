package com.lepine.transfers.data.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mappings({

    })
    Item toEntity(ItemUUIDLessDTO itemDTO);
}
