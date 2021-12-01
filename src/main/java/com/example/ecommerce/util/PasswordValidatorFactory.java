package com.example.ecommerce.util;

public class PasswordValidatorFactory {
    public static PasswordValidator create() {
        return new PasswordValidator();
    }
}
