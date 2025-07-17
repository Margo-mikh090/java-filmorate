package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {
    private static final String QUERY_ALL_DIRECTOR = "SELECT d.id, d.name FROM director d ORDER BY d.id";
    private static final String QUERY_DIRECTOR_BY_ID = "SELECT d.id, d.name FROM director d WHERE d.id = ?";
    private static final String INSERT_DIRECTOR = "INSERT INTO director (name) VALUES (?)";
    private static final String UPDATE_DIRECTOR = "UPDATE director SET name = ? WHERE id = ?";
    private static final String DELETE_DIRECTOR = "DELETE FROM director WHERE id = ?";
    private static final String INSERT_FILM_DIRECTOR = "INSERT INTO FILM_DIRECTOR(FILM_ID, DIRECTOR_ID) VALUES(?, ?)";
    private static final String DELETE_FILM_DIRECTORS = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?";
    private static final String QUERY_DIRECTOR_BY_FILM_ID =
            """
            SELECT d.id,
                   d.name
              FROM film_director fd
                   JOIN director d
                     ON d.id = fd.director_id
             WHERE fd.film_id = ?
             ORDER BY d.id
            """;
    private static final String QUERY_ALL_DIRECTOR_TO_FILM_ID =
            """
            SELECT fd.film_id,
                   d.id,
                   d.name
              FROM film_director fd
                   JOIN director d
                     ON d.id = fd.director_id
             ORDER BY fd.film_id, d.id
            """;

    public DirectorDbStorage(JdbcTemplate jdbc, DirectorRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Director> getAll() {
        return getAll(QUERY_ALL_DIRECTOR);
    }

    @Override
    public Director getById(long id) {
        return getById(QUERY_DIRECTOR_BY_ID, id);
    }

    @Override
    public void deleteById(long id) {
        deleteById(DELETE_DIRECTOR, id);
    }

    @Override
    public Director create(Director director) {
        long id = create(INSERT_DIRECTOR, director.getName());

        return getById(QUERY_DIRECTOR_BY_ID, id);
    }

    @Override
    public Director update(Director director) {
        update(UPDATE_DIRECTOR, director.getName(), director.getId());

        return getById(QUERY_DIRECTOR_BY_ID, director.getId());
    }

    @Override
    public void saveFilmDirectors(long filmId, Set<Director> directors) {
        jdbc.update(DELETE_FILM_DIRECTORS, filmId);

        if (directors != null && !directors.isEmpty()) {
            jdbc.batchUpdate(INSERT_FILM_DIRECTOR,
                    directors,
                    directors.size(),
                    (PreparedStatement ps, Director director) -> {
                        ps.setLong(1, filmId);
                        ps.setLong(2, director.getId());
                    });
        }
    }

    @Override
    public Set<Director> getByFilmId(long filmId) {
        return new HashSet<>(findMany(QUERY_DIRECTOR_BY_FILM_ID, filmId));
    }

    @Override
    public Map<Long, Set<Director>> findAllIndexByFilmId() {
        final Map<Long, Set<Director>> directorByFilmId = new HashMap<>();
        final RowMapper<Director> directorRowMapper = new DirectorRowMapper();

        jdbc.query(QUERY_ALL_DIRECTOR_TO_FILM_ID, (rs, rowNum) -> {
            long filmId = rs.getLong("film_id");
            Director director = directorRowMapper.mapRow(rs, rowNum);
            if (director != null) {
                if (directorByFilmId.containsKey(filmId)) {
                    directorByFilmId.get(filmId).add(director);
                } else {
                    directorByFilmId.put(filmId, new HashSet<>(List.of(director)));
                }
            }
            return null;
        });
        return directorByFilmId;
    }
}
