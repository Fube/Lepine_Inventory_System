package com.lepine.transfers.controllers.confirmation;

import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationUuidLessDTO;
import com.lepine.transfers.services.confirmation.ConfirmationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/confirmations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ConfirmationController {

    private final ConfirmationService confirmationService;

    @PostMapping
    public Confirmation create(
            @RequestBody @Valid ConfirmationUuidLessDTO confirmationUuidLessDto
    ) {

        final UUID transferUuid = confirmationUuidLessDto.getTransferUuid();
        final int quantity = confirmationUuidLessDto.getQuantity();
        log.info("Confirming {} for {}", transferUuid, quantity);

        return confirmationService.confirm(transferUuid, quantity);
    }
}
