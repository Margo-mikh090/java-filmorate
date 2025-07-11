package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.time.Instant;

@Data
public class Event {
    @NotNull
    private Long eventId;
    @NotNull
    private Long userId;
    @NotNull
    private Long entityId;
    private EventType eventType;
    private Operation operation;
    @NotNull
    private Instant timestamp;


    public Event(Long eventId, Long userId, Long entityId, EventType eventType, Operation operation, Instant timestamp) {
        this.eventId = eventId;
        this.userId = userId;
        this.entityId = entityId;
        this.eventType = eventType;
        this.operation = operation;
        this.timestamp = timestamp;
    }
}
