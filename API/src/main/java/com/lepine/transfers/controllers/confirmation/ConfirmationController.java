package com.lepine.transfers.controllers.confirmation;

import com.lepine.transfers.services.confirmation.ConfirmationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class ConfirmationController {

    private final ConfirmationService confirmationService;
}
