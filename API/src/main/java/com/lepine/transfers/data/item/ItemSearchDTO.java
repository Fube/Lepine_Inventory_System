package com.lepine.transfers.data.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemSearchDTO {

    private UUID objectID;
    private String name;
    private String sku;
    private String description;
}
