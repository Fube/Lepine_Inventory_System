package com.lepine.transfers.unit.services;

import com.lepine.transfers.config.SendGridConfig;
import com.lepine.transfers.services.mailer.MailerService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {})
@ActiveProfiles({"test"})
public class MailerServiceTests {

    private final static String
            VALID_FROM_ADDRESS = "valid@email.com",
            VALID_TO_ADDRESS = "valid2@email.com",
            VALID_SUBJECT = "Valid subject",
            VALID_BODY = "Valid body";
    private final static Email
            VALID_FROM_EMAIL = new Email(VALID_FROM_ADDRESS),
            VALID_TO_EMAIL = new Email(VALID_TO_ADDRESS);


    @Autowired
    private MailerService mailerService;

    @MockBean
    private SendGridConfig sendGridConfig;

    @MockBean
    private SendGrid sendGrid;

    @BeforeEach
    void setUp() {
        given(sendGridConfig.getApiKey()).willReturn("fake.api.key");
        given(sendGridConfig.getFrom()).willReturn(VALID_FROM_ADDRESS);
        given(sendGridConfig.getSendGrid()).willReturn(sendGrid);
    }

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("DjvQHxzQKw: Given a valid email address, when sending a mail, then the mail is sent")
    void valid_Mail() throws IOException {

        // Arrange
        final Content expectedContent = new Content("text/html", VALID_BODY);
        final Mail expectedMail = new Mail(VALID_FROM_EMAIL, VALID_SUBJECT, VALID_TO_EMAIL, expectedContent);

        final Request expectedRequest = new Request();
        expectedRequest.setMethod(Method.POST);
        expectedRequest.setEndpoint("mail/send");
        final String expectedBuilt = expectedMail.build();
        expectedRequest.setBody(expectedBuilt);

        given(sendGrid.api(expectedRequest)).willReturn(null);

        // Act
        final boolean result = mailerService.sendHTML(VALID_TO_ADDRESS, VALID_SUBJECT, VALID_BODY);

        // Assert
        assertThat(result).isTrue();

        verify(sendGrid, times(1))
                .api(argThat(request -> request.getMethod() == Method.POST &&
                        request.getEndpoint().equals("mail/send") &&
                        request.getBody().equals(expectedBuilt)));
    }

}
