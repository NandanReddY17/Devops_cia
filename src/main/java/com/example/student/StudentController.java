package com.example.student;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final List<Student> students = new CopyOnWriteArrayList<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @GetMapping
    public List<Student> getAllStudents() {
        return students;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student addStudent(@RequestBody Student input) {
        if (input.getName() == null || input.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student name is required");
        }
        Student student = new Student(nextId.getAndIncrement(), input.getName(), input.getCourse());
        students.add(student);
        return student;
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable int id) {
        return students.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }
}
