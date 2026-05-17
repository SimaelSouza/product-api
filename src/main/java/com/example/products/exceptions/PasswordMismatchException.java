package com.example.products.exceptions;

public class PasswordMismatchException extends BadRequestException {
    public PasswordMismatchException(String message) {
        super(message);
    }

    public PasswordMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
