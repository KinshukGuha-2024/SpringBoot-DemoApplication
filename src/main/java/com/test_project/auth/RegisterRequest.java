package com.test_project.auth;


import jakarta.validation.constraints.*;

public record RegisterRequest (
    @NotBlank(message = "First name is required!")
    String firstname,

    @NotBlank(message = "Last Name is required!")
    String lastname,

    @Email(message = "Please provide a valid email address!")
    @NotBlank(message = "Please provide your email address!")
    String email,

    @NotBlank(message = "Please provide a unique password!")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
        message = "Password must contain at least one uppercase, one lowercase, one number, one special character (@#$%^&+=!), and be at least 8 characters long"
    )
    String password,

    @NotBlank(message = "Your phone number is required!")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must be exactly 10 digits"
    )
    String phoneNumber
) {}
