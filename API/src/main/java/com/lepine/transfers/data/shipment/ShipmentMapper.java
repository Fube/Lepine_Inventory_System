package com.lepine.transfers.data.shipment;

import com.lepine.transfers.data.transfer.TransferMapper;
import com.lepine.transfers.data.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {TransferMapper.class})
public interface ShipmentMapper {

    @Mappings({
            @Mapping(target = "uuid", ignore = true),
    })
    Shipment toEntity(ShipmentStatusLessUuidLessDTO stockDTO);

    Shipment toEntity(ShipmentPatchDTO shipmentPatchDTO, @MappingTarget Shipment shipment);

    @Mappings({
            @Mapping(target = "createdBy", source = "user.uuid")
    })
    ShipmentStatusLessUuidLessDTO toStatusLessUuidLessDTO(
            ShipmentStatusLessCreatedByLessUuidLessDTO shipmentStatusLessCreatedByLessUuidLessDTO,
            User user);

    ShipmentPatchDTO toPatchDTO(Shipment shipment);
}
