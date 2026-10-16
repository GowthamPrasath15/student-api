package com.example.repository;

import com.example.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    // WHERE name = ?
    Optional<Department> findByName(String name);

    // WHERE block = ?
    List<Department> findByBlock(String block);

    // SELECT COUNT(*) > 0 WHERE name = ?
    boolean existsByName(String name);
}
