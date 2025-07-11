package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@RequiredArgsConstructor
public class BaseDbStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected List<T> getAll(String query) {
        return jdbc.query(query, mapper);
    }

    protected T getById(String query, long id) {
        T result;
        try {
            result = jdbc.queryForObject(query, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Данные не найдены");
        }
        return result;
    }

    protected void deleteById(String query, long id) {
        int deletedRecord = jdbc.update(query, id);
        if (deletedRecord == 0) {
            throw new NotFoundException("Данные не найдены");
        }
    }

    protected void update(String query, Object... params) {
        int updatedRecord = jdbc.update(query, params);
        if (updatedRecord == 0) {
            throw new NotFoundException("Не удалось обновить данные");
        }
    }

    protected Long create(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        return id;
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }
}
