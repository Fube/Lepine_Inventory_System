package com.lepine.transfers.data.warehouse;

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
public class WarehouseUUIDLessDTO {

    @NotNull(message = "{warehouse.zipcode.not_null}")
    @NotBlank(message = "{warehouse.zipcode.not_blank}")
    private String zipCode;
    private String city;
    private String province;
    private boolean isActive;
}
