package com.util;

import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

public interface MailService {
    public Map sendSimpleMail(JavaMailSender jms, String to, String subject, String content);
}
