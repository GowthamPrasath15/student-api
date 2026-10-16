package com.example.repository;

import com.example.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    // WHERE LOWER(title) LIKE LOWER('%?%')
    List<Course> findByTitleContainingIgnoreCase(String text);

    // WHERE credits >= ?
    List<Course> findByCreditsGreaterThanEqual(Integer credits);

    // WHERE department_id = ?
    List<Course> findByDepartmentId(Integer departmentId);

    // JOIN department WHERE department.name = ?
    List<Course> findByDepartmentName(String departmentName);

    // Total credits offered by one department (SUM returns Long in JPQL)
    @Query("SELECT SUM(c.credits) FROM Course c WHERE c.department.id = :deptId")
    Long totalCreditsByDepartment(@Param("deptId") Integer deptId);
}
