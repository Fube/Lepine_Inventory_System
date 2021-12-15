package com.lepine.transfers.data.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemUUIDLessDTO {

    @NotBlank(message = "{item.sku.not_blank}")
    private String sku;

    @NotBlank(message = "{item.name.not_blank}")
    private String name;

    @NotBlank(message = "{item.description.not_blank}")
    private String description;
}
