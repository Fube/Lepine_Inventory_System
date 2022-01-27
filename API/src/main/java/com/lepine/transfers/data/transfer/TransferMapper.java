package com.lepine.transfers.data.transfer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mappings({
            @Mapping(target = "uuid", ignore = true),
            @Mapping(target = "stock.uuid", source  = "stockUuid"),
    })
    Transfer toEntity(TransferUuidLessDTO transferDto);
}
