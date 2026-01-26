package com.application.filemanagement.service;

import com.application.filemanagement.dto.SignupRequest;
import com.application.filemanagement.entity.User;
import com.application.filemanagement.exceptions.EmailAlreadyExistsException;
import com.application.filemanagement.exceptions.PasswordMismatchException;
import com.application.filemanagement.exceptions.UserNotFoundException;
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
        user.setEnabled(false);
        user.setEmailVerified(false);
        userRepository.save(user);
    }


    // Method to activate the user after the email verification
    @Override
    public void activateUser(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) { throw new UserNotFoundException("User not found"); }
        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);
    }
}
