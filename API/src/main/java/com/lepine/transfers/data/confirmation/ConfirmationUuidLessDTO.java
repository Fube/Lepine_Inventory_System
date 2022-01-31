package com.lepine.transfers.data.confirmation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ConfirmationUuidLessDTO {

    @NotNull(message = "{transfer.uuid.not_null}")
    private UUID transferUuid;

    @Min(value = 1, message = "{transfer.quantity.min}")
    private int quantity;
}
