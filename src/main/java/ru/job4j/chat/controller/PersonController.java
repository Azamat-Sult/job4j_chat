package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.repository.PersonRepository;
import ru.job4j.chat.service.UpdateFieldsPartially;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonRepository personRepository;

    private final UpdateFieldsPartially service;

    private final BCryptPasswordEncoder encoder;

    public PersonController(PersonRepository personRepository,
                            UpdateFieldsPartially service,
                            BCryptPasswordEncoder encoder) {
        this.personRepository = personRepository;
        this.service = service;
        this.encoder = encoder;
    }

    @GetMapping("/")
    public List<Person> findAll() {
        return StreamSupport.stream(
                this.personRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        if (id < 1) {
            throw new NullPointerException("Person id can`t be less than 1");
        }
        var person = this.personRepository.findById(id);
        return new ResponseEntity<>(
                person.orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Person not found. Please, check id"
                        )
                ),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        if (person.getUsername() == null || person.getPassword() == null) {
            throw new NullPointerException("Username and password can`t be empty");
        }
        return new ResponseEntity<>(
                this.personRepository.save(person),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        if (person.getUsername() == null || person.getPassword() == null) {
            throw new NullPointerException("Username and password can`t be empty");
        }
        this.personRepository.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (id < 1) {
            throw new NullPointerException("Person id can`t be less than 1");
        }
        Person person = new Person();
        person.setId(id);
        this.personRepository.delete(person);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public void signUp(@Valid @RequestBody Person person) {
        if (person.getUsername() == null || person.getPassword() == null) {
            throw new NullPointerException("Username and password can`t be empty");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        personRepository.save(person);
    }

    @PatchMapping("/")
    public ResponseEntity<Person> patchPerson(@RequestBody Person person)
            throws InvocationTargetException, IllegalAccessException {
        return new ResponseEntity<>(
                this.service.updateFieldsPartially(personRepository, person),
                HttpStatus.OK
        );
    }

}