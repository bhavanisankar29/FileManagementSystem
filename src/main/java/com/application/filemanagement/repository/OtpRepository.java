package com.application.filemanagement.repository;

import com.application.filemanagement.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    void deleteByEmail(String email);
    Optional<OtpEntity> findByEmailAndOtp(String email, String otp);
}
