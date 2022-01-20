package com.lepine.transfers.data.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TransferUuidLessDTO {

    private UUID stockUuid;
    private int quantity;;
}
