package com.example.exception;

// Thrown when the server itself cannot read/write a file (disk full, permissions...)
// Not handled specifically on purpose: the catch-all handler logs it and returns a generic 500.
public class FileStorageException extends RuntimeException {
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
