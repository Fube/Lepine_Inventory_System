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
public class WarehouseActiveLessUUIDLessDTO {

    @NotNull(message = "Zipcode must not be null")
    @NotBlank(message = "Zipcode must not be blank")
    private String zipCode;

    @NotNull(message = "City must not be null")
    @NotBlank(message = "City must not be blank")
    private String city;

    @NotNull(message = "Province must not be null")
    @NotBlank(message = "Province must not be blank")
    private String province;
}
