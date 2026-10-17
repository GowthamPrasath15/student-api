package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.StudentRequest;
import com.example.dto.StudentResponse;
import com.example.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller is thin: validate -> call service -> wrap in ApiResponse.
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> create(@Valid @RequestBody StudentRequest request) {
        StudentResponse created = studentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Students fetched successfully", studentService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Student fetched successfully", studentService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> update(@PathVariable Integer id,
                                                               @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully",
                studentService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        studentService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Student deleted successfully", null));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getByDepartment(@PathVariable Integer departmentId) {
        return ResponseEntity.ok(ApiResponse.success("Students fetched successfully",
                studentService.getByDepartment(departmentId)));
    }
}
