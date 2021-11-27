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
import ru.job4j.chat.domain.Role;
import ru.job4j.chat.repository.RoleRepository;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Job4jChatApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void resetDb() {
        repository.deleteAll();
    }

    @Test
    public void findAllRolesTest() throws Exception {

        Role role1 = Role.of("admin");
        repository.save(role1);

        Role role2 = Role.of("moderator");
        repository.save(role2);

        Role role3 = Role.of("user");
        repository.save(role3);

        mockMvc.perform(get("/role/"))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                Arrays.asList(role1, role2, role3)))
                );

    }

    @Test
    public void findByIdFoundTest() throws Exception {

        Role role1 = Role.of("admin");
        Role savedRole = repository.save(role1);

        mockMvc.perform(get("/role/{id}", savedRole.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRole.getId()))
                .andExpect(jsonPath("$.role").value(savedRole.getRole()));

    }

    @Test
    public void findByIdNotFoundTest() throws Exception {

        mockMvc.perform(get("/role/{id}", 1))
                .andExpect(status().isNotFound());

    }

    @Test
    public void createRoleTest() throws Exception {

        Role role1 = Role.of("admin");

        mockMvc.perform(post("/role/")
                        .content(objectMapper.writeValueAsString(role1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.role").value("admin"));

    }

    @Test
    public void updateRoleTest() throws Exception {

        Role oldRole = Role.of("admin");
        Role savedOldRole = repository.save(oldRole);

        Role newRole = Role.of("user");
        newRole.setId(savedOldRole.getId());

        mockMvc.perform(put("/role/")
                        .content(objectMapper.writeValueAsString(newRole))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        Role updatedRole = repository.findById(savedOldRole.getId()).get();

        Assertions.assertEquals(updatedRole.getId(), newRole.getId());
        Assertions.assertEquals(updatedRole.getRole(), newRole.getRole());

    }

    @Test
    public void deleteRoleTest() throws Exception {

        Role roleToDelete = Role.of("admin");
        Role savedRoleToDelete = repository.save(roleToDelete);

        mockMvc.perform(delete("/role/{id}", savedRoleToDelete.getId()))
                .andExpect(status().isOk());

        Assertions.assertTrue(repository.findById(savedRoleToDelete.getId()).isEmpty());

    }

}