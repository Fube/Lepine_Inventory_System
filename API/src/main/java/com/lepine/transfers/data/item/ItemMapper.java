package com.lepine.transfers.data.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mappings({
        @Mapping(target = "uuid", ignore = true),
    })
    Item toEntity(ItemUUIDLessDTO itemDTO);

    @Mappings({
            @Mapping(target = "objectID", source = "uuid"),
    })
    ItemSearchDTO toSearchDTO(Item item);
}
