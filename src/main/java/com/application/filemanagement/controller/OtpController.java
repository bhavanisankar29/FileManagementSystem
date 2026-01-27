package com.application.filemanagement.controller;

import com.application.filemanagement.service.OtpService;
import com.application.filemanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OtpController {

    private final OtpService otpService;
    private final UserService userService;

    public OtpController(OtpService otpService, UserService userService) {
        this.otpService = otpService;
        this.userService = userService;
    }

    // FOR PASSWORD RESET

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

    // FOR EMAIL VERIFICATION

    @GetMapping("/verify-email")
    public String verifyEmail(Model model,
                              HttpServletRequest  request) {
        String email = (String) request.getSession().getAttribute("VERIFY_EMAIL");
        model.addAttribute("email", email);
        return "verify-email";
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String email,
                              @RequestParam String otp,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest  request) {
        boolean isValid = otpService.verifyOtp(email, otp);
        if(!isValid){
            return "redirect:/verify-email?error";
        }
        userService.activateUser(email);
        request.getSession().removeAttribute("VERIFY_EMAIL");
        redirectAttributes.addFlashAttribute("verificationSuccess", "Verification Success");
        return "redirect:/login";
    }

    @PostMapping("/resend-otp")
    public String resendOtp(HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        String email = (String) request.getSession().getAttribute("VERIFY_EMAIL");
        if(email == null){ return "redirect:/login"; }
        otpService.sendOtp(email);
        redirectAttributes.addFlashAttribute("OTPsent", "OTP resent successfully");
        return "redirect:/verify-email?resend=success";
    }
}
