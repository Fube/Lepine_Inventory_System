package com.lepine.transfers.data.shipment;

import com.lepine.transfers.data.transfer.TransferMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {TransferMapper.class})
public interface ShipmentMapper {

    @Mappings({
            @Mapping(target = "uuid", ignore = true),
    })
    Shipment toEntity(ShipmentStatusLessUuidLessDTO stockDTO);
}
