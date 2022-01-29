package com.lepine.transfers.services.mailer;

public interface MailerService {
    boolean send(String to, String subject, String content);
}
