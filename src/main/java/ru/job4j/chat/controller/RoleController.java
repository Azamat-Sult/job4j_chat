package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Role;
import ru.job4j.chat.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping("/")
    public List<Role> findAll() {
        return StreamSupport.stream(
                this.roleRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> findById(@PathVariable int id) {
        if (id < 1) {
            throw new NullPointerException("Role id can`t be less than 1");
        }
        var role = this.roleRepository.findById(id);
        return new ResponseEntity<>(
                role.orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Role not found. Please, check id"
                        )
                ),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    public ResponseEntity<Role> create(@RequestBody Role role) {
        if (role.getRole() == null) {
            throw new NullPointerException("Role can`t be empty");
        }
        return new ResponseEntity<>(
                this.roleRepository.save(role),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Role role) {
        if (role.getRole() == null) {
            throw new NullPointerException("Role can`t be empty");
        }
        this.roleRepository.save(role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (id < 1) {
            throw new NullPointerException("Role id can`t be less than 1");
        }
        Role role = new Role();
        role.setId(id);
        this.roleRepository.delete(role);
        return ResponseEntity.ok().build();
    }

}