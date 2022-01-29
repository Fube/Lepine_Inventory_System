package com.lepine.transfers.services.mailer;

import org.springframework.stereotype.Service;

@Service
public class SendgridMailer implements MailerService {
    @Override
    public boolean sendHTML(String to, String subject, String content) {
        return false;
    }
}
