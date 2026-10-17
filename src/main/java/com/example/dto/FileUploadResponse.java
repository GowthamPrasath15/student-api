package com.example.dto;

// What the client RECEIVES after an upload.
public record FileUploadResponse(
        String fileName,        // name the file is stored under (use this in GET/DELETE /files/{fileName})
        String originalName,    // name the client uploaded it with
        String contentType,     // as reported by the client
        long size,              // bytes
        String downloadUrl) {
}
