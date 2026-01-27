package com.application.filemanagement.service;

import com.application.filemanagement.dto.MailRequest;
import com.application.filemanagement.entity.OtpEntity;
import com.application.filemanagement.repository.OtpRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private EmailService emailService;

    @Transactional
    public void sendOtp(String email) {

        // Delete previous OTPs for this email
        otpRepository.deleteByEmail(email);

        // Generate 6-digit OTP
        String otp = String.valueOf(new SecureRandom().nextInt(900000) + 100000);

        // Save OTP in database
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otpEntity);

        emailService.sendOtpMail(email, otp);
    }

    public boolean verifyOtp(String email, String otp){
        // Check if OTP-ENTITY with the given email & otp is in the database
        Optional<OtpEntity> otpEntity = otpRepository.findByEmailAndOtp(email, otp);
        if(otpEntity.isEmpty()) return false;
        OtpEntity otpEntity1 = otpEntity.get();
        if(otpEntity1.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpEntity1);
            return false;
        }
        // Delete OTP it because it is one-time use
        otpRepository.delete(otpEntity1);
        return true;

    }
}
