package com.example.dto;

import com.example.entity.Student;

// What the client RECEIVES. Flat, and only the fields we choose to expose.
// The entity never leaves the service layer.
public record StudentResponse(
        Integer id,
        String firstName,
        String lastName,
        String email,
        Integer age,
        Integer departmentId,
        String departmentName) {

    // Entity -> DTO mapper
    public static StudentResponse from(Student s) {
        return new StudentResponse(
                s.getId(),
                s.getFirstName(),
                s.getLastName(),
                s.getEmail(),
                s.getAge(),
                s.getDepartment().getId(),
                s.getDepartment().getName());
    }
}
