package com.application.filemanagement.controller;

import com.application.filemanagement.dto.MailRequest;
import com.application.filemanagement.dto.SignupRequest;
import com.application.filemanagement.exceptions.EmailAlreadyExistsException;
import com.application.filemanagement.exceptions.PasswordMismatchException;
import com.application.filemanagement.service.EmailService;
import com.application.filemanagement.service.OtpService;
import com.application.filemanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;
    private final OtpService otpService;

    public AuthController(UserService userService, EmailService emailService, OtpService otpService) {
        this.userService = userService;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @GetMapping("/")
    public String homePage() { return "homepage";}

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupRequest signupRequest,
                         Model model,
                         HttpServletRequest request) {
        try {
            userService.registerUser(signupRequest);

            emailService.sendWelcomeMail(signupRequest.getEmail(), signupRequest.getFullname()); // Send welcome mail to the user
            otpService.sendOtp(signupRequest.getEmail()); // Send email-verification-code to the user

            request.getSession().setAttribute("VERIFY_EMAIL", signupRequest.getEmail());
            return "redirect:/verify-email";
        } catch (EmailAlreadyExistsException e) {
            model.addAttribute("emailAlreadyExists", e.getMessage());
            return "signup";
        } catch (PasswordMismatchException e) {
            model.addAttribute("passwordMismatch", e.getMessage());
            return "signup";
        }
    }
}
