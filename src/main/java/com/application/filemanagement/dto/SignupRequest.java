package com.application.filemanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {

    private String fullname;
    private String email;
    private String password;
    private String confirmPassword;

}
