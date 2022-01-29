package com.lepine.transfers.unit.notifier;

import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentStatus;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.events.shipment.ShipmentUpdateEvent;
import com.lepine.transfers.notification.Notifier;
import com.lepine.transfers.services.mailer.MailerService;
import com.lepine.transfers.services.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes= {
        Notifier.class,
})
@ActiveProfiles({"test"})
public class NotifierTests {

    private final static UUID
        VALID_USER_UUID =  UUID.randomUUID();

    private final static String
        VALID_USER_EMAIL = "valid@email.com";

    private final static User
        VALID_USER = User.builder()
            .uuid(VALID_USER_UUID)
            .email(VALID_USER_EMAIL)
            .build();

    @Autowired
    private Notifier notifier;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private UserService userService;

    @MockBean
    private MailerService mailerService;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("BMWhlJynWR: Given shipment update, trigger onShipmentUpdate")
    void valid_TriggerShipmentUpdate() {

        // Arrange
        final Shipment expectedOldShipment = Shipment.builder()
                .uuid(UUID.randomUUID())
                .createdBy(VALID_USER_UUID)
                .status(ShipmentStatus.PENDING)
                .build();

        final Shipment expectedNewShipment = expectedOldShipment.toBuilder()
                .status(ShipmentStatus.ACCEPTED)
                .build();

        final ShipmentUpdateEvent expectedUpdateShipmentEvent =
                new ShipmentUpdateEvent(this, expectedOldShipment, expectedNewShipment);

        final String expectedSubject = "Shipment status update";
        final String expectedBody = "Your shipment has been accepted";

        given(userService.findByUuid(VALID_USER_UUID)).willReturn(Optional.of(VALID_USER));
        given(mailerService.sendHTML(VALID_USER_EMAIL, expectedSubject, expectedBody))
                .willReturn(true);

        // Act
        applicationEventPublisher.publishEvent(expectedUpdateShipmentEvent);

        // Assert
        verify(userService, times(1)).findByUuid(VALID_USER_UUID);
        verify(mailerService, times(1))
                .sendHTML(VALID_USER_EMAIL, expectedSubject, expectedBody);
    }

    @Test
    @DisplayName("eiSoWSJgxZ: Given shipment with no status update, do nothing")
    void valid_NoShipmentUpdate() {

        // Arrange
        final Shipment expectedOldShipment = Shipment.builder()
                .uuid(UUID.randomUUID())
                .createdBy(VALID_USER_UUID)
                .status(ShipmentStatus.PENDING)
                .build();

        final Shipment expectedNewShipment = expectedOldShipment.toBuilder()
                .status(ShipmentStatus.PENDING)
                .build();

        final ShipmentUpdateEvent expectedUpdateShipmentEvent =
                new ShipmentUpdateEvent(this, expectedOldShipment, expectedNewShipment);

        // Act
        applicationEventPublisher.publishEvent(expectedUpdateShipmentEvent);

        // Assert
        verify(userService, never()).findByUuid(any());
        verify(mailerService, never()).sendHTML(any(), any(), any());
    }

    @Test
    @DisplayName("ZPjZwUveJJ: Given unable to find user, do nothing")
    void invalid_NoUser() {

        // Arrange
        final Shipment expectedOldShipment = Shipment.builder()
                .uuid(UUID.randomUUID())
                .createdBy(VALID_USER_UUID)
                .status(ShipmentStatus.PENDING)
                .build();

        final Shipment expectedNewShipment = expectedOldShipment.toBuilder()
                .status(ShipmentStatus.ACCEPTED)
                .build();

        final ShipmentUpdateEvent expectedUpdateShipmentEvent =
                new ShipmentUpdateEvent(this, expectedOldShipment, expectedNewShipment);

        given(userService.findByUuid(VALID_USER_UUID)).willReturn(Optional.empty());

        // Act
        applicationEventPublisher.publishEvent(expectedUpdateShipmentEvent);

        // Assert
        verify(userService, times(1)).findByUuid(VALID_USER_UUID);
        verify(mailerService, never()).sendHTML(any(), any(), any());
    }
}
