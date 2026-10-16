package com.example.exception;

// Thrown when a unique value already exists (e.g. email)  -> HTTP 409
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
