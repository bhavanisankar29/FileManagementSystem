package com.application.filemanagement.service;

import com.application.filemanagement.dto.MailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ HTML OTP email
    public void sendOtpMail(MailRequest mailRequest) {
        sendHtmlMail(mailRequest);
    }

    // ✅ HTML Welcome email (UPDATED)
    public void sendWelcomeMail(MailRequest mailRequest) {
        sendHtmlMail(mailRequest);
    }

    // 🔁 Common reusable HTML mail sender
    private void sendHtmlMail(MailRequest mailRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(mailRequest.getTo());
            helper.setSubject(mailRequest.getSubject());
            helper.setText(mailRequest.getMessage(), true); // true = HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}