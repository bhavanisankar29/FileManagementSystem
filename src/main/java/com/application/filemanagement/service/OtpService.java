package com.application.filemanagement.service;

import com.application.filemanagement.dto.MailRequest;
import com.application.filemanagement.entity.OtpEntity;
import com.application.filemanagement.repository.OtpRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private EmailService emailService;

    @Transactional
    public void sendOtp(String email) {

        // 1. Delete previous OTPs for this email
        otpRepository.deleteByEmail(email);

        // 2. Generate 6-digit OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        // 3. Save OTP in database
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otpEntity);

        // 4. Prepare HTML email content
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
                                    <p>You requested to reset your password. Use the OTP below:</p>
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

        // 5. Send OTP email
        MailRequest mailRequest = new MailRequest();
        mailRequest.setTo(email);
        mailRequest.setSubject("OTP to reset password");
        mailRequest.setMessage(htmlMessage);

        emailService.sendOtpMail(mailRequest);
    }


    public boolean verifyOtp(String email, String otp){
        // Check if OTP-ENTITY with the given email, otp in the database
        Optional<OtpEntity> otpEntity = otpRepository.findByEmailAndOtp(email, otp);
        if(otpEntity.isEmpty()) return false;
        OtpEntity otpEntity1 = otpEntity.get();
        if(otpEntity1.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpEntity1);
            return false;
        }
        // We delete it because it is one-time use
        otpRepository.delete(otpEntity1);
        return true;

    }
}
