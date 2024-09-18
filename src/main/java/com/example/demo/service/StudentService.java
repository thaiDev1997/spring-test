package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.StudentNotFoundException;
import com.example.demo.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student addStudent(Student student) {
        Boolean existsEmail = studentRepository
                .selectExistsEmail(student.getEmail());
        if (existsEmail) throw new BadRequestException("Email " + student.getEmail() + " taken");

        return studentRepository.save(student);
    }

    public void deleteStudent(Long studentId) {
        if(!studentRepository.existsById(studentId)) throw new StudentNotFoundException("Student with id " + studentId +
                " does not exists");
        studentRepository.deleteById(studentId);
    }
}
