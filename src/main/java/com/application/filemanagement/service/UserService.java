package com.application.filemanagement.service;

import com.application.filemanagement.dto.SignupRequest;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface UserService {
    void registerUser(@ModelAttribute SignupRequest signupRequest);
    void activateUser(String email);
}
