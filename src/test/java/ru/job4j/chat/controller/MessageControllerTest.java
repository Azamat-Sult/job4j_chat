package ru.job4j.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.chat.Job4jChatApplication;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.repository.MessageRepository;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Job4jChatApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessageControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void resetDb() {
        messageRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin")
    public void findAllMessagesTest() throws Exception {

        Message message1 = Message.of("message 1", null);
        messageRepository.save(message1);
        Message message2 = Message.of("message 2", null);
        messageRepository.save(message2);
        Message message3 = Message.of("message 3", null);
        messageRepository.save(message3);

        mockMvc.perform(get("/message/"))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                Arrays.asList(message1, message2, message3)))
                );

    }

    @Test
    @WithMockUser(username = "admin")
    public void findByIdFoundTest() throws Exception {

        Message message1 = Message.of("message 1", null);
        Message savedMessage1 = messageRepository.save(message1);

        mockMvc.perform(get("/message/{id}", savedMessage1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedMessage1.getId()))
                .andExpect(jsonPath("$.text").value(savedMessage1.getText()));

    }

    @Test
    @WithMockUser(username = "admin")
    public void findByIdNotFoundTest() throws Exception {

        mockMvc.perform(get("/message/{id}", 1))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(username = "admin")
    public void createMessageTest() throws Exception {

        Message message1 = Message.of("message 1", null);

        mockMvc.perform(post("/message/")
                        .content(objectMapper.writeValueAsString(message1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.text").value(message1.getText()));

    }

    @Test
    @WithMockUser(username = "admin")
    public void updateMessageTest() throws Exception {

        Message oldMessage = Message.of("old message 1", null);
        Message savedOldMessage = messageRepository.save(oldMessage);

        Message newMessage = Message.of("new message 2", null);
        newMessage.setId(savedOldMessage.getId());

        mockMvc.perform(put("/message/")
                        .content(objectMapper.writeValueAsString(newMessage))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        Message updatedMessage = messageRepository.findById(savedOldMessage.getId()).get();

        Assertions.assertEquals(updatedMessage.getId(), newMessage.getId());
        Assertions.assertEquals(updatedMessage.getText(), newMessage.getText());

    }

    @Test
    @WithMockUser(username = "admin")
    public void deleteMessageTest() throws Exception {

        Message message = Message.of("message 1", null);
        Message savedMessage = messageRepository.save(message);

        mockMvc.perform(delete("/message/{id}", savedMessage.getId()))
                .andExpect(status().isOk());

        Assertions.assertTrue(messageRepository.findById(savedMessage.getId()).isEmpty());

    }

}