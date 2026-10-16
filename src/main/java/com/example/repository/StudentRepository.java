package com.example.repository;

import com.example.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    // ---------- 1. Derived queries: Spring builds the SQL from the method name ----------

    // WHERE last_name = ?
    List<Student> findByLastName(String lastName);

    // WHERE email = ?   (Optional because email is unique)
    Optional<Student> findByEmail(String email);

    // WHERE age > ?
    List<Student> findByAgeGreaterThan(Integer age);

    // WHERE age BETWEEN ? AND ?
    List<Student> findByAgeBetween(Integer min, Integer max);

    // WHERE LOWER(first_name) LIKE LOWER('%?%')
    List<Student> findByFirstNameContainingIgnoreCase(String text);

    // SELECT COUNT(*) > 0 WHERE email = ?
    boolean existsByEmail(String email);

    // ---------- 2. Navigating the foreign key (student -> department) ----------

    // WHERE department_id = ?
    List<Student> findByDepartmentId(Integer departmentId);

    // JOIN department WHERE department.name = ?
    List<Student> findByDepartmentName(String departmentName);

    // same as above + ORDER BY last_name ASC
    List<Student> findByDepartmentNameOrderByLastNameAsc(String departmentName);

    // SELECT COUNT(*) WHERE department_id = ?
    long countByDepartmentId(Integer departmentId);

    // ---------- 3. Custom JPQL (uses entity/field names, not table/column names) ----------

    @Query("SELECT s FROM Student s WHERE s.department.name = :deptName AND s.age >= :minAge")
    List<Student> findByDepartmentAndMinAge(@Param("deptName") String deptName,
                                            @Param("minAge") Integer minAge);

    // ---------- 4. Native SQL (uses real table/column names) ----------

    @Query(value = "SELECT * FROM student WHERE age > :age ORDER BY age DESC", nativeQuery = true)
    List<Student> findOlderThanNative(@Param("age") Integer age);
}
