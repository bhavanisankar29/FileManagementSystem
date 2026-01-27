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

    // HTML OTP email
    public void sendOtpMail(String email, String otp) {


        String htmlMessage = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>OTP Verification</title>
        </head>
        <body style="margin:0; padding:0; background-color:#f4f6f8; font-family: Arial, sans-serif;">
            <table width="100%%" cellpadding="0" cellspacing="0">
                <tr>
                    <td align="center" style="padding: 40px 0;">
                        <table width="500" cellpadding="0" cellspacing="0"
                               style="background:#ffffff; border-radius:8px; box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                            <tr>
                                <td style="padding: 24px; text-align:center;">
                                    <h2 style="margin:0; color:#24292e;">Password Reset OTP</h2>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding: 0 24px 24px; color:#57606a; font-size:14px;">
                                    <p>Hello,</p>
                                    <p>Use the OTP below:</p>
                                    <div style="margin: 24px 0; text-align:center;">
                                        <span style="
                                            display:inline-block;
                                            padding: 12px 24px;
                                            font-size:24px;
                                            letter-spacing:4px;
                                            background:#f6f8fa;
                                            border-radius:6px;
                                            color:#24292e;
                                            font-weight:bold;">
                                            %s
                                        </span>
                                    </div>
                                    <p>This OTP is valid for <strong>5 minutes</strong>.</p>
                                    <p>If you didn’t request this, you can safely ignore this email.</p>
                                    <p style="margin-top:32px;">— File Management System Team</p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """.formatted(otp);

        MailRequest mailRequest = new MailRequest();
        mailRequest.setTo(email);
        mailRequest.setSubject("OTP for verification");
        mailRequest.setMessage(htmlMessage);

        sendHtmlMail(mailRequest);
    }

    // HTML Welcome email
    public void sendWelcomeMail(String email, String fullname) {

        String htmlMessage = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Welcome</title>
                </head>
                <body style="margin:0; padding:0; background-color:#f4f6f8; font-family: Arial, sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0">
                        <tr>
                            <td align="center" style="padding: 40px 0;">
                                <table width="500" cellpadding="0" cellspacing="0"
                                       style="background:#ffffff; border-radius:8px; box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                                    <tr>
                                        <td style="padding: 24px; text-align:center;">
                                            <h2 style="margin:0; color:#24292e;">
                                                Welcome, %s
                                            </h2>
                                        </td>
                                    </tr>
            
                                    <tr>
                                        <td style="padding: 0 24px 24px; color:#57606a; font-size:14px;">
                                            <p>Hi <strong>%s</strong>,</p>
            
                                            <p>
                                                Thanks for registering on our website!  
                                                We’re really happy to have you on board.
                                            </p>
            
                                            <p>
                                                You can now explore all the features and start using the platform.
                                            </p>
            
                                            <p style="margin-top:32px;">
                                                Cheers,<br>
                                                <strong>File Management System Team</strong>
                                            </p>
                                        </td>
                                    </tr>
            
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(fullname, fullname);
        MailRequest mailRequest = new MailRequest();
        mailRequest.setTo(email);
        mailRequest.setSubject("Thanks for registering on our website!");
        mailRequest.setMessage(htmlMessage);

        sendHtmlMail(mailRequest);
    }

    // Common reusable HTML mail sender
    private void sendHtmlMail(MailRequest mailRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(mailRequest.getTo());
            helper.setSubject(mailRequest.getSubject());
            helper.setText(mailRequest.getMessage(), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}