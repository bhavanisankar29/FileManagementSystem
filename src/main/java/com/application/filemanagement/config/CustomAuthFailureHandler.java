package com.application.filemanagement.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException {

        String email = request.getParameter("username"); // email field name

        if (exception instanceof DisabledException) {
            // Redirect inactive users to OTP verification
            request.getSession().setAttribute("VERIFY_EMAIL", email);
            response.sendRedirect("/verify-email");
            return;
        }

        // Default behavior
        response.sendRedirect("/login?error");
    }
}
