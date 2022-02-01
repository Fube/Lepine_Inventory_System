package com.lepine.transfers.services.confirmation;

import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationRepo;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatus;
import com.lepine.transfers.data.transfer.Transfer;
import com.lepine.transfers.data.transfer.TransferRepo;
import com.lepine.transfers.exceptions.shipment.ShipmentNotAcceptedException;
import com.lepine.transfers.exceptions.shipment.ShipmentNotFoundException;
import com.lepine.transfers.exceptions.transfer.QuantityExceededException;
import com.lepine.transfers.exceptions.transfer.TransferNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ConfirmationServiceImpl implements ConfirmationService {

    private final ConfirmationRepo confirmationRepo;
    private final TransferRepo transferRepo;
    private final ShipmentRepo shipmentRepo;

    @Override
    public Confirmation confirm(final UUID transferUuid, final int quantity) {
        log.info("Confirming {} of transfer {}", quantity, transferUuid);

        log.info("Looking for transfer");
        Transfer transfer = transferRepo.findById(transferUuid)
                .orElseThrow(() -> new TransferNotFoundException(transferUuid));
        log.info("Found transfer with quantity {}", transfer.getQuantity());

        log.info("Checking if shipment is ACCEPTED");
        final Optional<Shipment> byTransferUuid = shipmentRepo.findByTransferUuid(transferUuid);
        if (byTransferUuid.isEmpty()) {
            log.info("Shipment is not found");
            throw new ShipmentNotFoundException(format("Shipment for transfer %s is not found", transferUuid));
        }

        final ShipmentStatus status = byTransferUuid.get().getStatus();
        if (status != ShipmentStatus.ACCEPTED) {
            log.info("Shipment is not ACCEPTED");
            throw new ShipmentNotAcceptedException(transferUuid, status.name());
        }

        Integer alreadyConfirmed = confirmationRepo.sumQuantityByTransferUuid(transferUuid);

        if(alreadyConfirmed == null) {
            alreadyConfirmed = 0;
        }

        if (alreadyConfirmed + quantity > transfer.getQuantity()) {
            log.error("Transfer quantity {} is less than confirmation quantity {}", transfer.getQuantity(), quantity);
            throw new QuantityExceededException(transfer.getQuantity(), quantity);
        }

        log.info("Confirming transfer");
        final Confirmation confirmation = confirmationRepo.save(Confirmation.builder()
                .transferUuid(transferUuid)
                .quantity(quantity)
                .build());
        log.info("Transfer confirmed");

        return confirmation;
    }
}
