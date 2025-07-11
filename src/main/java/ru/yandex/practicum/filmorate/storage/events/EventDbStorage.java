package ru.yandex.practicum.filmorate.storage.events;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.List;

@Repository
public class EventDbStorage implements  EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventRowMapper eventRowMapper;


    public EventDbStorage(JdbcTemplate jdbcTemplate, EventRowMapper eventRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventRowMapper = eventRowMapper;
    }

    @Override
    public List<Event> getAllEventsByUserId(long userId) {
        String sql = "SELECT * FROM event_feed WHERE user_id = ?";
        return jdbcTemplate.query(sql, eventRowMapper, userId);
    }


}
