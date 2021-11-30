package ru.job4j.chat.domain;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
public class Person extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 0, message = "Id must not be negative")
    private int id;
    @NotBlank(message = "Username must be not empty")
    private String username;
    @NotBlank(message = "Password must be not empty")
    private String password;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    private boolean enabled = true;

    public static Person of(String username, String password, Role role) {
        Person newPerson = new Person();
        newPerson.username = username;
        newPerson.password = password;
        newPerson.role = role;
        return newPerson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", username='" + username
                + "', password='" + password + "', role=" + role + '}';
    }
}