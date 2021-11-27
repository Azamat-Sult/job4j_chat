package ru.job4j.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.chat.Job4jChatApplication;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.repository.PersonRepository;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Job4jChatApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersonControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void resetDb() {
        personRepository.deleteAll();
    }

    @Test
    public void findAllPersonsTest() throws Exception {

        Person person1 = Person.of("person 1", "password 1", null);
        personRepository.save(person1);

        Person person2 = Person.of("person 2", "password 2", null);
        personRepository.save(person2);

        Person person3 = Person.of("person 3", "password 3", null);
        personRepository.save(person3);

        mockMvc.perform(get("/person/"))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                Arrays.asList(person1, person2, person3)))
                );

    }

    @Test
    public void findByIdFoundTest() throws Exception {

        Person person1 = Person.of("person 1", "password 1", null);
        Person savedPerson1 = personRepository.save(person1);

        mockMvc.perform(get("/person/{id}", savedPerson1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPerson1.getId()))
                .andExpect(jsonPath("$.username").value(savedPerson1.getUsername()))
                .andExpect(jsonPath("$.password").value(savedPerson1.getPassword()));

    }

    @Test
    public void findByIdNotFoundTest() throws Exception {

        mockMvc.perform(get("/person/{id}", 1))
                .andExpect(status().isNotFound());

    }

    @Test
    public void createPersonTest() throws Exception {

        Person person1 = Person.of("person 1", "password 1", null);

        mockMvc.perform(post("/person/")
                        .content(objectMapper.writeValueAsString(person1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value(person1.getUsername()))
                .andExpect(jsonPath("$.password").value(person1.getPassword()));

    }

    @Test
    public void updatePersonTest() throws Exception {

        Person oldPerson = Person.of("old person 1", "old password 1", null);
        Person savedOldPerson = personRepository.save(oldPerson);

        Person newPerson = Person.of("new person 2", "new password 2", null);
        newPerson.setId(savedOldPerson.getId());

        mockMvc.perform(put("/person/")
                        .content(objectMapper.writeValueAsString(newPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        Person updatedPerson = personRepository.findById(savedOldPerson.getId()).get();

        Assertions.assertEquals(updatedPerson.getId(), newPerson.getId());
        Assertions.assertEquals(updatedPerson.getUsername(), newPerson.getUsername());
        Assertions.assertEquals(updatedPerson.getPassword(), newPerson.getPassword());

    }

    @Test
    public void deletePersonTest() throws Exception {

        Person personToDelete = Person.of("person 1", "password 1", null);
        Person savedPersonToDelete = personRepository.save(personToDelete);

        mockMvc.perform(delete("/person/{id}", savedPersonToDelete.getId()))
                .andExpect(status().isOk());

        Assertions.assertTrue(personRepository.findById(savedPersonToDelete.getId()).isEmpty());

    }

}