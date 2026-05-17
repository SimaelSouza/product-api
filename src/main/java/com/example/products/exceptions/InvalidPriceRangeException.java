package com.example.products.exceptions;

public class InvalidPriceRangeException extends BadRequestException {
    public InvalidPriceRangeException(String message) {
        super(message);
    }

    public InvalidPriceRangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
