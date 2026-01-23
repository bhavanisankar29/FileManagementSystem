package com.application.filemanagement.controller;

import com.application.filemanagement.dto.MailRequest;
import com.application.filemanagement.dto.SignupRequest;
import com.application.filemanagement.exceptions.EmailAlreadyExistsException;
import com.application.filemanagement.exceptions.PasswordMismatchException;
import com.application.filemanagement.service.EmailService;
import com.application.filemanagement.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;
    public AuthController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
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
                         Model model) {
        try {
            userService.registerUser(signupRequest);
            // Create MailRequest Object and send welcome email to the user
            MailRequest mailRequest = new MailRequest();
            mailRequest.setTo(signupRequest.getEmail());
            mailRequest.setSubject("Thanks for registering");
            mailRequest.setMessage("""
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
                """.formatted(signupRequest.getFullname(), signupRequest.getFullname()));

            emailService.sendWelcomeMail(mailRequest);

            return "redirect:/login?Success";
        } catch (EmailAlreadyExistsException e) {
            model.addAttribute("emailAlreadyExists", e.getMessage());
            return "signup";
        } catch (PasswordMismatchException e) {
            model.addAttribute("passwordMismatch", e.getMessage());
            return "signup";
        }
    }
}
