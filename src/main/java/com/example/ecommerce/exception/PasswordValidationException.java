package com.example.ecommerce.exception;

public class PasswordValidationException extends RuntimeException {

    public PasswordValidationException(String exception) {
        super(exception);
    }
}
