package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.FileUploadResponse;
import com.example.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// Thin controller again: all the work happens in FileStorageService.
@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // POST /files   (Content-Type: multipart/form-data, field name = "file")
    // required = false so a missing field reaches the service and gives our own friendly 400 message
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponse>> upload(
            @RequestParam(value = "file", required = false) MultipartFile file) {

        FileUploadResponse uploaded = fileStorageService.store(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("File uploaded successfully", uploaded));
    }

    // GET /files/{fileName}  -> the file itself (images and PDFs open in the browser)
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable String fileName) {
        Resource resource = fileStorageService.load(fileName);

        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(resource.getFilename()).build().toString())
                .body(resource);
    }

    // DELETE /files/{fileName}
    @DeleteMapping("/{fileName}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String fileName) {
        fileStorageService.delete(fileName);
        return ResponseEntity.ok(ApiResponse.<Void>success("File deleted successfully", null));
    }
}
