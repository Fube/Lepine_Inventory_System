package com.lepine.transfers.data.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class WarehouseActiveLessUUIDLessDTO {

    private String zipCode;
    private String city;
    private String province;
}
