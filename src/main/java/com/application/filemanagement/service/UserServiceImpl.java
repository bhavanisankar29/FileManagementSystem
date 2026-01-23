package com.application.filemanagement.service;

import com.application.filemanagement.dto.SignupRequest;
import com.application.filemanagement.entity.User;
import com.application.filemanagement.exceptions.EmailAlreadyExistsException;
import com.application.filemanagement.exceptions.PasswordMismatchException;
import com.application.filemanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Method to register user and save in the database
    @Override
    public void registerUser(SignupRequest signupRequest) {
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists, Try again");
        }
        // Check if passwords in the both input fields are same or not
        if(!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            throw new PasswordMismatchException("Password Mismatch");
        }

        User user = new User();
        user.setFullname(signupRequest.getFullname());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        userRepository.save(user);
    }
}
