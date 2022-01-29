package com.lepine.transfers.unit.services;

import com.lepine.transfers.services.mailer.MailerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {})
@ActiveProfiles({"test"})
public class MailerServiceTests {

    private final static String
            VALID_ADDRESS = "valid@email.com",
            VALID_SUBJECT = "Valid subject",
            VALID_BODY = "Valid body";

    @Autowired
    private MailerService mailerService;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("DjvQHxzQKw: Given a valid email address, when sending a mail, then the mail is sent")
    void valid_Mail() {

        // Arrange
        given(mailerService.sendHTML(VALID_ADDRESS, VALID_SUBJECT, VALID_BODY))
                .willReturn(true);

        // Act
        final boolean result = mailerService.sendHTML(VALID_ADDRESS, VALID_SUBJECT, VALID_BODY);

        // Assert
        assertThat(result).isTrue();
    }

}
