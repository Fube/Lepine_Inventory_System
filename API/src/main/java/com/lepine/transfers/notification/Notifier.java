package com.lepine.transfers.notification;

import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.events.shipment.ShipmentUpdateEvent;
import com.lepine.transfers.events.shipment.ShipmentUpdateHandler;
import com.lepine.transfers.services.mailer.MailerService;
import com.lepine.transfers.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
@Slf4j
public class Notifier implements ShipmentUpdateHandler {

    private final UserService userService;
    private final MailerService mailerService;

    @Override
    public void onShipmentCreate(ShipmentUpdateEvent event) {

        log.info("Reacting to shipment update event");

        final Shipment old = event.getOld();
        final Shipment current = event.getUpdated();

        // NOTE: In the future, we might care about the diff between the old and the current shipment
        //      but for now, we just care if the shipment is accepted or denied. No need to over-engineer this
        if(old.getStatus() == current.getStatus()) {
            log.info("Uninterested in change, ignoring");
            return;
        }

        final UUID createdBy = current.getCreatedBy();
        final Optional<User> affectedUser = userService.findByUuid(createdBy);

        if(affectedUser.isEmpty()) {
            log.info("No user found for uuid {}, ignoring", createdBy);
            return;
        }

        final String to = affectedUser.get().getEmail();
        log.info("Sending email to {}", to);
        final boolean emailSent = mailerService.sendHTML(
                to,
                "Shipment status update",
                format("Your shipment has been %s", current.getStatus().name().toLowerCase()));

        if(!emailSent) {
            log.error("Failed to send email to {}", to);
        } else {
            log.info("Email sent to {}", to);
        }
    }
}
