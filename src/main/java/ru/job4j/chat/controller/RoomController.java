package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.domain.Room;
import ru.job4j.chat.repository.PersonRepository;
import ru.job4j.chat.repository.RoomRepository;
import ru.job4j.chat.service.UpdateFieldsPartially;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RestTemplate rest;

    private final PersonRepository personRepository;

    private final RoomRepository roomRepository;

    private final UpdateFieldsPartially service;

    private static final String MESSAGE_API = "http://localhost:8080/message/";

    private static final String MESSAGE_API_ID = "http://localhost:8080/message/{id}";

    public RoomController(PersonRepository personRepository,
                          RoomRepository roomRepository,
                          UpdateFieldsPartially service) {
        this.personRepository = personRepository;
        this.roomRepository = roomRepository;
        this.service = service;
    }

    @GetMapping("/")
    public List<Room> findAll() {
        return StreamSupport.stream(
                this.roomRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> findById(@PathVariable int id) {
        if (id < 1) {
            throw new NullPointerException("Room id can`t be less than 1");
        }
        var room = this.roomRepository.findById(id);
        return new ResponseEntity<>(
                room.orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Room not found. Please, check id"
                        )
                ),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    public ResponseEntity<Room> create(@Valid @RequestBody Room room) {
        if (room.getName() == null || room.getDescription() == null) {
            throw new NullPointerException("Room name and description can`t be empty");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        room.setOwner(personRepository.findByUsername(auth.getName()));
        return new ResponseEntity<>(
                this.roomRepository.save(room),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Room room) {
        if (room.getName() == null || room.getDescription() == null) {
            throw new NullPointerException("Room name and description can`t be empty");
        }
        this.roomRepository.save(room);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (id < 1) {
            throw new NullPointerException("Room id can`t be less than 1");
        }
        Room room = new Room();
        room.setId(id);
        this.roomRepository.delete(room);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/")
    public ResponseEntity<Room> patchRoom(@Valid @RequestBody Room room)
            throws InvocationTargetException, IllegalAccessException {
        return new ResponseEntity<>(
                this.service.updateFieldsPartially(roomRepository, room),
                HttpStatus.OK
        );
    }

    @PostMapping("/{id}/addMessage")
    public ResponseEntity<Room> addMessageToRoom(@PathVariable int id,
                                                 @Valid @RequestBody Message message,
                                                 @RequestHeader("Authorization") String token) {

        Room room = findById(id).getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", token);
        HttpEntity<Message> httpEntity = new HttpEntity<>(message, headers);

        Message newMessage = rest.postForObject(MESSAGE_API, httpEntity, Message.class);
        room.addMessage(newMessage);

        return new ResponseEntity<>(
                this.roomRepository.save(room),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{roomId}/deleteMessage/{msgId}")
    public ResponseEntity<Room> deleteMsgInRoom(@PathVariable int roomId,
                                                @PathVariable int msgId,
                                                @RequestHeader("Authorization") String token) {

        Room room = findById(roomId).getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", token);
        HttpEntity<Message> httpEntity = new HttpEntity<>(headers);

        try {
            Message message = rest.exchange(
                    MESSAGE_API_ID,
                    HttpMethod.GET,
                    httpEntity,
                    Message.class,
                    msgId
            ).getBody();
            room.delMessage(message);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Message not found. Please, check id"
                );
            }
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new NullPointerException("Message id can`t be less than 1");
            }
        }

        return new ResponseEntity<>(
                this.roomRepository.save(room),
                HttpStatus.OK
        );
    }

}