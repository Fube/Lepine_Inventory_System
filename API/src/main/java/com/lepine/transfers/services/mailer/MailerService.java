package com.lepine.transfers.services.mailer;

public interface MailerService {
    boolean sendHTML(String to, String subject, String content);
}
