package com.lepine.transfers.services.confirmation;

import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationRepo;
import com.lepine.transfers.data.transfer.TransferRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ConfirmationServiceImpl implements ConfirmationService {

    private final ConfirmationRepo confirmationRepo;
    private final TransferRepo transferRepo;

    @Override
    public Confirmation confirm(UUID transferUuid, int quantity) {
        throw new RuntimeException("Not implemented");
    }
}
