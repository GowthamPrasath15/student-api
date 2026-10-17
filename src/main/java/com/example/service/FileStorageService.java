package com.example.service;

import com.example.dto.FileUploadResponse;
import com.example.exception.FileStorageException;
import com.example.exception.InvalidRequestException;
import com.example.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Saves uploaded files into a folder on disk (app.upload-dir) and reads / deletes them again.
 *
 * Safety rules (good to discuss with students):
 *  1. The client's file name is never used as-is: we store  <random-uuid>_<cleaned-name>.
 *  2. Only whitelisted extensions are accepted.
 *  3. Every path is normalised and must stay INSIDE the upload folder (blocks "../" tricks).
 */
@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png", "gif", "pdf", "txt", "csv", "docx", "xlsx");

    private final Path uploadPath;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload folder: " + uploadPath, e);
        }
    }

    public FileUploadResponse store(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("No file received. Send a form-data field named 'file'.");
        }

        // Some browsers send a full path (C:\Users\me\a.png): keep only the last part
        String originalName = StringUtils.getFilename(StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "" : file.getOriginalFilename()));

        if (originalName == null || originalName.isBlank() || originalName.contains("..")) {
            throw new InvalidRequestException("Invalid file name");
        }

        String extension = StringUtils.getFilenameExtension(originalName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
            throw new InvalidRequestException(
                    "File type not allowed. Allowed types: " + String.join(", ", new java.util.TreeSet<>(ALLOWED_EXTENSIONS)));
        }

        // Keep only safe characters, then prefix a random id so two uploads never overwrite each other
        String safeName = originalName.replaceAll("[^A-Za-z0-9._-]", "_");
        String storedName = UUID.randomUUID() + "_" + safeName;

        Path target = resolveInsideUploadFolder(storedName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + storedName, e);
        }

        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/").path(storedName).toUriString();

        return new FileUploadResponse(storedName, originalName, file.getContentType(), file.getSize(), downloadUrl);
    }

    public Resource load(String fileName) {
        Path file = resolveInsideUploadFolder(fileName);
        if (!Files.isRegularFile(file)) {
            throw new ResourceNotFoundException("File not found: " + fileName);
        }
        return new FileSystemResource(file);
    }

    public void delete(String fileName) {
        Path file = resolveInsideUploadFolder(fileName);
        if (!Files.isRegularFile(file)) {
            throw new ResourceNotFoundException("File not found: " + fileName);
        }
        try {
            Files.delete(file);
        } catch (IOException e) {
            throw new FileStorageException("Could not delete file " + fileName, e);
        }
    }

    // Turns a name into a path and refuses anything that escapes the upload folder.
    private Path resolveInsideUploadFolder(String fileName) {
        try {
            Path file = uploadPath.resolve(fileName).normalize();
            if (!file.startsWith(uploadPath)) {
                throw new InvalidRequestException("Invalid file name");
            }
            return file;
        } catch (InvalidPathException e) {   // e.g. characters Windows does not allow in a path
            throw new InvalidRequestException("Invalid file name");
        }
    }
}
