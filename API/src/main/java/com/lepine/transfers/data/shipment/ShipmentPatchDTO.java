package com.lepine.transfers.data.shipment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ShipmentPatchDTO {

    @NotNull(message = "{shipment.patch.status.not_null}")
    @NotBlank(message = "{shipment.patch.status.not_blank}")
    private ShipmentStatus status;
}
