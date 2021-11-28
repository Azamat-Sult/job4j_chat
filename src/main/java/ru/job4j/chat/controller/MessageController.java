package ru.job4j.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.repository.MessageRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageRepository messageRepository;

    private final ObjectMapper objectMapper;

    public MessageController(MessageRepository messageRepository,
                             ObjectMapper objectMapper) {
        this.messageRepository = messageRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public List<Message> findAll() {
        return StreamSupport.stream(
                this.messageRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Message> findById(@PathVariable int id) {
        if (id < 1) {
            throw new NullPointerException("Message id can`t be less than 1");
        }
        var message = this.messageRepository.findById(id);
        return new ResponseEntity<>(
                message.orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Message not found. Please, check id"
                        )
                ),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    public ResponseEntity<Message> create(@RequestBody Message message) {
        if (message.getText() == null) {
            throw new NullPointerException("Message text can`t be empty");
        }
        if (message.getText().length() > 255) {
            throw new IllegalArgumentException(
                    "The message is too long (Max 255 characters)."
                            + " Please split the message into multiple messages");
        }
        return new ResponseEntity<>(
                this.messageRepository.save(message),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Message message) {
        if (message.getText() == null) {
            throw new NullPointerException("Message text can`t be empty");
        }
        if (message.getText().length() > 255) {
            throw new IllegalArgumentException(
                    "The message is too long (Max 255 characters)."
                            + " Please split the message into multiple messages");
        }
        this.messageRepository.save(message);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (id < 1) {
            throw new NullPointerException("Message id can`t be less than 1");
        }
        Message message = new Message();
        message.setId(id);
        this.messageRepository.delete(message);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
    }

}