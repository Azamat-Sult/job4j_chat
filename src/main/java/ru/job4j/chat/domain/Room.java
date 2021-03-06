package ru.job4j.chat.domain;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Room extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 0, message = "Id must not be negative")
    private int id;
    @NotBlank(message = "Room name must be not empty")
    private String name;
    @NotBlank(message = "Room description must be not empty")
    private String description;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Person owner;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date(System.currentTimeMillis());
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("id ASC")
    private List<Message> messages = new ArrayList<>();

    public static Room of(String name, String description, Person owner) {
        Room newRoom = new Room();
        newRoom.name = name;
        newRoom.description = description;
        newRoom.owner = owner;
        return newRoom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void delMessage(Message message) {
        this.messages.remove(message);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Room room = (Room) o;
        return id == room.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Room{" + "id=" + id + ", name='" + name
                + "', description='" + description + "', owner=" + owner
                + ", created=" + created + ", messages=" + messages + '}';
    }

}