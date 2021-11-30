package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Room;
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

    private final RoomRepository roomRepository;

    private final UpdateFieldsPartially service;

    public RoomController(RoomRepository roomRepository,
                          UpdateFieldsPartially service) {
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

}