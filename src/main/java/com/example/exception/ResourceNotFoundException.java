package com.example.exception;

// Thrown when something we look up by id does not exist  -> HTTP 404
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
