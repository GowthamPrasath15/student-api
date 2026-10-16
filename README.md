# student-api

A small Spring Boot + PostgreSQL project for teaching REST, JPA and a production-style response format.

**Stack:** Java 17, Spring Boot 4.0.8, Spring Data JPA (Hibernate), Bean Validation, PostgreSQL 16, Maven

## Database

```
department (1) ---< student (many)
department (1) ---< course  (many)
```

`db/school_schema.sql` creates the 3 tables and loads 3 departments, 8 students and 6 courses.

## Run it

**1. Start PostgreSQL** (needs Docker). The schema and sample data load automatically on first start:

```bash
docker compose up -d
```

To reset the data: `docker compose down -v` and then `docker compose up -d`.

No Docker? Create a database called `school` in your own Postgres and run `db/school_schema.sql` in it,
then adjust the credentials in `src/main/resources/application.properties`.

**2. Start the application**

- Eclipse / STS: **File > Import > Maven > Existing Maven Projects**, pick this folder, then right-click
  `DemoApplication` > **Run As > Java Application**.
- Command line (Maven installed): `mvn spring-boot:run`

The API runs on <http://localhost:8080>.

## Endpoints

| Method | URL                               | What it does                    |
|--------|-----------------------------------|---------------------------------|
| POST   | `/students`                       | Create a student                |
| GET    | `/students`                       | List all students               |
| GET    | `/students/{id}`                  | Get one student                 |
| GET    | `/students/department/{deptId}`   | List students of one department |

```bash
curl -X POST http://localhost:8080/students \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ravi","lastName":"Kumar","email":"ravi.kumar@college.edu","age":21,"departmentId":1}'

curl http://localhost:8080/students
curl http://localhost:8080/students/1
curl http://localhost:8080/students/department/3
```

## Response format

Every endpoint returns the same envelope, for success and for errors:

```json
{
  "success": true,
  "message": "Student created successfully",
  "data": { "id": 9, "firstName": "Ravi", "lastName": "Kumar", "email": "ravi.kumar@college.edu",
            "age": 21, "departmentId": 1, "departmentName": "Computer Science" },
  "timestamp": "2026-10-10T07:05:12Z"
}
```

| Situation                     | HTTP status |
|-------------------------------|-------------|
| Created                       | 201         |
| Validation failed / bad JSON  | 400         |
| Student or department missing | 404         |
| Duplicate email               | 409         |
| Unexpected error              | 500         |

## Project layout

```
src/main/java/com/example
├── DemoApplication.java        entry point (must stay in the root package)
├── controller/                 HTTP layer: validate, call service, wrap in ApiResponse
├── service/                    business logic + entity <-> DTO mapping
├── repository/                 Spring Data JPA interfaces (derived queries, JPQL, native SQL)
├── entity/                     JPA entities (Department, Student, Course)
├── dto/                        ApiResponse envelope, StudentRequest, StudentResponse
└── exception/                  custom exceptions + GlobalExceptionHandler
db/school_schema.sql            tables + sample data
docker-compose.yml              PostgreSQL for local development
```

## Teaching order (suggested)

1. Entities and the foreign key (`@ManyToOne`)
2. Repository methods: derived queries, then `@Query` JPQL, then native SQL
3. A plain controller returning an entity
4. Request / response DTOs and why entities should not leave the service layer
5. `ApiResponse`, `@Valid` and the global exception handler
