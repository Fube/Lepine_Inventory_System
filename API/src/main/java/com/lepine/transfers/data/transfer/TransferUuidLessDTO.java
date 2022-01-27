package com.lepine.transfers.data.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TransferUuidLessDTO {

    private UUID stockUuid;

    @Min(value = 1, message = "{transfer.quantity.min}")
    private int quantity;
}
