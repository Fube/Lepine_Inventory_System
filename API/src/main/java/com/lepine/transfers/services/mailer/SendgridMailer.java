package com.lepine.transfers.services.mailer;

import com.lepine.transfers.config.SendGridConfig;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendgridMailer implements MailerService {

    private final SendGrid sendGrid;
    private final SendGridConfig sendGridConfig;

    @Override
    public boolean sendHTML(String to, String subject, String content) {

        final Email fromEmail = new Email(sendGridConfig.getFrom());
        final Email toEmail = new Email(to);
        final Content contentEmail = new Content("text/html", content);

        final Mail mail = new Mail(fromEmail, subject, toEmail, contentEmail);

        final Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sendGrid.api(request);
        } catch (IOException e) {
            log.error("Error sending email", e);
            return false;
        }

        return true;
    }
}
