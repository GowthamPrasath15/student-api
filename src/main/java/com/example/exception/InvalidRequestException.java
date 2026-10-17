package com.example.exception;

// Thrown when the client sent something unacceptable (empty file, bad file type...)  -> HTTP 400
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
