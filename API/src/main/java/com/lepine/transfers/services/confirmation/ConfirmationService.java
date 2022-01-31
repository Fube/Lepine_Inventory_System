package com.lepine.transfers.services.confirmation;

import com.lepine.transfers.data.confirmation.Confirmation;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public interface ConfirmationService {
    Confirmation confirm(
            @NotNull(message = "transfer.uuid.not_null") final UUID transferUuid,
            final int quantity
    );
}
