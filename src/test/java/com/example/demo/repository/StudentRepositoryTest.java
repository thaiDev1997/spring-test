package com.example.demo.repository;

import com.example.demo.constant.Gender;
import com.example.demo.entity.Student;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@FieldDefaults(level = AccessLevel.PRIVATE)
@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    StudentRepository underTest;

    @AfterEach
    public void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldCheckIfStudentExistsEmail() {
        // given
        String email = "johndoe@gmail.com";
        Student student = Student.builder().name("John Doe").email(email).gender(Gender.FEMALE).build();
        underTest.save(student);

        // when
        boolean result = underTest.selectExistsEmail(email);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void itShouldCheckIfStudentNotExistsEmail() {
        // given
        String email = "johndoe@gmail.com";

        // when
        boolean result = underTest.selectExistsEmail(email);

        // then
        assertThat(result).isFalse();
    }
}