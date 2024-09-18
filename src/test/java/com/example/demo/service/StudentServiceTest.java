package com.example.demo.service;

import com.example.demo.constant.Gender;
import com.example.demo.entity.Student;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.StudentNotFoundException;
import com.example.demo.repository.StudentRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    StudentRepository studentRepository;
    StudentService underTest;

    @BeforeEach
    void setup() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        // when
        underTest.getAllStudents();

        // then
        // verify whether studentRepository::find() is invoked inside StudentService::getAllStudents or not
        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        // Given
        String email = "johndoe@gmail.com";
        Student student = Student.builder().name("John Doe").email(email).gender(Gender.FEMALE).build();

        // When
        underTest.addStudent(student);

        // Then
        // + create a capture on Student class
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
        // + verify StudentRepository::save() and capture Student object which was passed into save(student)
        verify(studentRepository).save(studentArgumentCaptor.capture());
        // + get Student object was captured at save(student)
        Student studentCapture = studentArgumentCaptor.getValue();
        // + assertion
        assertThat(studentCapture).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // Given
        Student student = Student.builder().name("John Doe").email("johndoe@gmail.com").gender(Gender.FEMALE).build();

        // When & Then
        Mockito.when(studentRepository.selectExistsEmail(student.getEmail())).thenReturn(true);
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        // verify never call studentRepository::save()
        verify(studentRepository, never()).save(Mockito.any());
    }

    @Test
    void canDeleteStudent() {
        // Given
        Long studentId = Mockito.anyLong();
        // When
        Mockito.when(studentRepository.existsById(studentId)).thenReturn(true);
        underTest.deleteStudent(studentId);
        // Then
        verify(studentRepository).deleteById(Mockito.any());
    }

    @Test
    void willThrowWhenIdIsNotTakenYet() {
        // Given
        Long studentId = Mockito.anyLong();
        // When - Then
        Mockito.when(studentRepository.existsById(studentId)).thenReturn(false);
        assertThatThrownBy(() -> underTest.deleteStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exists");

        verify(studentRepository, never()).deleteById(Mockito.any());
    }
}