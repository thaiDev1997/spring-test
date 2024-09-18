package com.example.demo.controller;

import com.example.demo.constant.Gender;
import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AutoConfigureMockMvc
@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StudentService studentService;

    @Test
    void getAllStudents() throws Exception {
        // When - Then
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON_VALUE));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void canAddStudent() throws Exception {
        // Given
        Student student = Student.builder().name("John Doe").email("johndoe@gmail.com").gender(Gender.FEMALE).build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // WHEN
        when(studentService.addStudent(Mockito.any(Student.class))).thenReturn(student);
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(student)));

        // THEN
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("name").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("name", CoreMatchers.is(student.getName())));
    }

    @Test
    void canDeleteStudent() throws Exception {
        // WHEN
        doNothing().when(studentService).deleteStudent(Mockito.anyLong());
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/students/" + 0));

        // THEN
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}