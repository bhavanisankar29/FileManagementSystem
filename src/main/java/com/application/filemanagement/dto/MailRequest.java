package com.application.filemanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailRequest {
    private String to;
    private String subject;
    private String message;
}
