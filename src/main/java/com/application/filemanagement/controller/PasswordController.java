package com.application.filemanagement.controller;

import com.application.filemanagement.entity.User;
import com.application.filemanagement.repository.UserRepository;
import com.application.filemanagement.service.OtpService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordController {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    public PasswordController(UserRepository userRepository,
                              OtpService otpService,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {return "forgot-password";}

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email,
                                       Model model) {
        User user = userRepository.findByEmail(email);
        if(user == null){
            model.addAttribute("noUserFound", "Invalid email");
            return "forgot-password";
        }
        // send otp id user email is legit and then redirect to verify-otp.html
        otpService.sendOtp(email);
        model.addAttribute("userEmail", email);
        return "verify-otp";
    }

    @GetMapping("/verify-otp")
    public String verifyOtp() { return "verify-otp";}

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            Model model) {

        // Check if entered OTP is valid or not
        boolean isValid = otpService.verifyOtp(email, otp);
        if(!isValid){
            model.addAttribute("userEmail", email);
            model.addAttribute("wrongOtp", "Invalid otp");
            return "verify-otp";
        }
        model.addAttribute("userEmail", email);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam String confirmPassword,
                                      Model model) {
        User user = userRepository.findByEmail(email);
        // Check if password in the both input fields are same
        if(!password.equals(confirmPassword)){
            model.addAttribute("passwordError", "Passwords do not match");
            return "reset-password";
        }
        // Save the encoded password
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return "redirect:/login";
    }
}
