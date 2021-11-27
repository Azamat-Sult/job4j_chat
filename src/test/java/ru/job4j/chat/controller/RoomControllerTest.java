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
import ru.job4j.chat.domain.Room;
import ru.job4j.chat.repository.RoomRepository;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Job4jChatApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoomControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void resetDb() {
        roomRepository.deleteAll();
    }

    @Test
    public void findAllRoomsTest() throws Exception {

        Room room1 = Room.of("room 1", "description 1", null);
        roomRepository.save(room1);
        Room room2 = Room.of("room 2", "description 2", null);
        roomRepository.save(room2);
        Room room3 = Room.of("room 3", "description 3", null);
        roomRepository.save(room3);

        mockMvc.perform(get("/room/"))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                Arrays.asList(room1, room2, room3)))
                );

    }

    @Test
    public void findByIdFoundTest() throws Exception {

        Room room1 = Room.of("room 1", "description 1", null);
        Room savedRoom1 = roomRepository.save(room1);

        mockMvc.perform(get("/room/{id}", savedRoom1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRoom1.getId()))
                .andExpect(jsonPath("$.name").value(savedRoom1.getName()))
                .andExpect(jsonPath("$.description").value(savedRoom1.getDescription()));

    }

    @Test
    public void findByIdNotFoundTest() throws Exception {

        mockMvc.perform(get("/room/{id}", 1))
                .andExpect(status().isNotFound());

    }

    @Test
    public void createRoomTest() throws Exception {

        Room room1 = Room.of("room 1", "description 1", null);

        mockMvc.perform(post("/room/")
                        .content(objectMapper.writeValueAsString(room1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(room1.getName()))
                .andExpect(jsonPath("$.description").value(room1.getDescription()));

    }

    @Test
    public void updateRoomTest() throws Exception {

        Room oldRoom = Room.of("old room 1", "old description 1", null);
        Room savedOldRoom = roomRepository.save(oldRoom);

        Room newRoom = Room.of("new room 1", "new description 1", null);
        newRoom.setId(oldRoom.getId());

        mockMvc.perform(put("/room/")
                        .content(objectMapper.writeValueAsString(newRoom))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        Room updatedRoom = roomRepository.findById(savedOldRoom.getId()).get();

        Assertions.assertEquals(updatedRoom.getId(), newRoom.getId());
        Assertions.assertEquals(updatedRoom.getName(), newRoom.getName());
        Assertions.assertEquals(updatedRoom.getDescription(), newRoom.getDescription());

    }

    @Test
    public void deleteRoomTest() throws Exception {

        Room room1 = Room.of("room 1", "description 1", null);
        Room savedRoom = roomRepository.save(room1);

        mockMvc.perform(delete("/room/{id}", savedRoom.getId()))
                .andExpect(status().isOk());

        Assertions.assertTrue(roomRepository.findById(savedRoom.getId()).isEmpty());

    }

}