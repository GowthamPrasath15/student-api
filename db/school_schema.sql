-- =====================================================
-- Simple school database for teaching Spring Boot + JPA
-- department (1) ---< student (many)
-- department (1) ---< course  (many)
-- Safe to re-run: it drops and recreates everything.
-- =====================================================

DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS course CASCADE;
DROP TABLE IF EXISTS department CASCADE;

-- 1. Parent table
CREATE TABLE department (
    id    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(50)  NOT NULL UNIQUE,
    block VARCHAR(20)
);

-- 2. Child table (FK -> department)
CREATE TABLE student (
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    age           INT,
    department_id INT NOT NULL,
    CONSTRAINT fk_student_department
        FOREIGN KEY (department_id) REFERENCES department(id)
);

-- 3. Child table (FK -> department)
CREATE TABLE course (
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title         VARCHAR(100) NOT NULL,
    credits       INT NOT NULL,
    department_id INT NOT NULL,
    CONSTRAINT fk_course_department
        FOREIGN KEY (department_id) REFERENCES department(id)
);

-- ---------------------- DATA -------------------------

-- ids will be 1, 2, 3 (fresh identity after the drop above)
INSERT INTO department (name, block) VALUES
    ('Computer Science', 'Block A'),
    ('Mechanical',       'Block B'),
    ('Aeronautical',     'Block C');

INSERT INTO student (first_name, last_name, email, age, department_id) VALUES
    ('Arun',   'Kumar',   'arun.kumar@college.edu',   20, 1),
    ('Priya',  'Sharma',  'priya.sharma@college.edu', 21, 1),
    ('Karthik','Raj',     'karthik.raj@college.edu',  22, 1),
    ('Divya',  'Nair',    'divya.nair@college.edu',   20, 2),
    ('Suresh', 'Babu',    'suresh.babu@college.edu',  21, 2),
    ('Meena',  'Iyer',    'meena.iyer@college.edu',   19, 3),
    ('Vignesh','Selvam',  'vignesh.selvam@college.edu',22, 3),
    ('Anitha', 'Devi',    'anitha.devi@college.edu',  20, 3);

INSERT INTO course (title, credits, department_id) VALUES
    ('Data Structures',        4, 1),
    ('Database Systems',       3, 1),
    ('Thermodynamics',         4, 2),
    ('Fluid Mechanics',        3, 2),
    ('Aerodynamics',           4, 3),
    ('Aircraft Propulsion',    3, 3);

-- ---------------- QUICK CHECKS -----------------------
-- SELECT * FROM department;
-- SELECT s.first_name, d.name FROM student s JOIN department d ON s.department_id = d.id;
-- SELECT c.title, d.name FROM course c JOIN department d ON c.department_id = d.id;
