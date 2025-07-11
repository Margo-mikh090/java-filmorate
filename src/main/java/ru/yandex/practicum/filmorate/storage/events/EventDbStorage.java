package ru.yandex.practicum.filmorate.storage.events;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbc;
    private final EventRowMapper mapper;
    private static final String ADD_EVENT = "INSERT INTO event_feed (user_id, entity_id, event_type, operation) " +
            "VALUES (?, ?, ?, ?)";
    private static final String GET_ALL_EVENTS_BY_USER_ID = "SELECT event_id, user_id, entity_id, event_type, " +
            "operation, created_at FROM event_feed WHERE user_id = ?";

    @Override
    public void addEvent(Event event) {
        jdbc.update(ADD_EVENT,
                event.getUserId(),
                event.getEntityId(),
                event.getEventType().name(),
                event.getOperation().name());
    }

    @Override
    public List<Event> getAllEventsByUserId(long userId) {
        return jdbc.query(GET_ALL_EVENTS_BY_USER_ID, mapper, userId);
    }
}
