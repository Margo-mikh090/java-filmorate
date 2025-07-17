package ru.yandex.practicum.filmorate.storage.events;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;

import java.util.List;

@Repository
public class EventDbStorage extends BaseDbStorage<Event> implements EventStorage {
    private static final String ADD_EVENT = "INSERT INTO event_feed (user_id, entity_id, event_type, operation) " +
            "VALUES (?, ?, ?, ?)";
    private static final String GET_ALL_EVENTS_BY_USER_ID = "SELECT event_id, user_id, entity_id, event_type, " +
            "operation, created_at FROM event_feed WHERE user_id = ?";

    public EventDbStorage(JdbcTemplate jdbc, EventRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addEvent(Event event) {
        update(ADD_EVENT,
                event.getUserId(),
                event.getEntityId(),
                event.getEventType().name(),
                event.getOperation().name());
    }

    @Override
    public List<Event> getAllEventsByUserId(long userId) {
        List<Event> events = findMany(GET_ALL_EVENTS_BY_USER_ID, userId);
        if (events.isEmpty()) {
            throw new NotFoundException("Событий по пользователю не найдено");
        }
        return events;
    }
}