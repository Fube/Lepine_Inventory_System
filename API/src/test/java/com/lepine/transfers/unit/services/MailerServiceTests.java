package com.lepine.transfers.unit.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {})
@ActiveProfiles({"test"})
public class MailerServiceTests {

    private final static String
            VALID_EMAIL = "valid@email.com",
            VALID_SUBJECT = "Valid subject",
            VALID_BODY = "Valid body";

    @Autowired
    private MailerService mailerService;

    @MockBean
    private Mailer mailer;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("DjvQHxzQKw: Given a valid email address, when sending a mail, then the mail is sent")
    void valid_Mail() {

        // Arrange
        given(mailer.send(VALID_EMAIL, "subject", "body"))
                .willReturn(true);

        // Act
        final boolean result = mailerService.send(VALID_EMAIL, VALID_SUBJECT, VALID_BODY);

        // Assert
        assertThat(result).isTrue();
    }

}
