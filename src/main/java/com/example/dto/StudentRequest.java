package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// What the client SENDS. Validated automatically because the controller uses @Valid.
public record StudentRequest(

        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        String email,

        @NotNull(message = "age is required")
        @Min(value = 16, message = "age must be at least 16")
        @Max(value = 60, message = "age must be at most 60")
        Integer age,

        @NotNull(message = "departmentId is required")
        Integer departmentId) {
}
