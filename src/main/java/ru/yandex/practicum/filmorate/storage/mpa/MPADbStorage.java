package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.MPARowMapper;

import java.util.Collection;

@Repository
public class MPADbStorage extends BaseDbStorage<MPA> {
    private static final String GET_BY_ID = "SELECT * FROM mpa WHERE id = ?";
    private static final String GET_ALL = "SELECT * FROM mpa";

    public MPADbStorage(JdbcTemplate jdbc, MPARowMapper mapper) {
        super(jdbc, mapper);
    }

    public Collection<MPA> getAll() {
        return getAll(GET_ALL);
    }

    public MPA getById(long id) {
        return getById(GET_BY_ID, id);
    }
}
