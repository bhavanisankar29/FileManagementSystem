package com.application.filemanagement.exceptions;

public class InvalidFilePathExtension extends RuntimeException {
    public InvalidFilePathExtension(String message) {
        super(message);
    }
}
