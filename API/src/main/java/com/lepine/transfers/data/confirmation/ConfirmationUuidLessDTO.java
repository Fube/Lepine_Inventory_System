package com.lepine.transfers.data.confirmation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ConfirmationUuidLessDTO {

    private UUID transferUuid;
    private int quantity;
}
