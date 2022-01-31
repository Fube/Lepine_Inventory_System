package com.lepine.transfers.services.confirmation;

import java.util.UUID;

public interface ConfirmationService {
    void confirm(final UUID transferUuid, final int quantity);
}
