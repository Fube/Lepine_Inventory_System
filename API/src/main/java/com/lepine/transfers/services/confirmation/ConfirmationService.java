package com.lepine.transfers.services.confirmation;

import com.lepine.transfers.data.confirmation.Confirmation;

import java.util.UUID;

public interface ConfirmationService {
    Confirmation confirm(final UUID transferUuid, final int quantity);
}
