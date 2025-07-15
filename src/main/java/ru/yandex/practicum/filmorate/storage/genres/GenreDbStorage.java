package ru.yandex.practicum.filmorate.storage.genres;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {

    private static final String GET_BY_ID = "SELECT * FROM genres WHERE id = ?";
    private static final String GET_ALL = "SELECT * FROM genres";
    private static final String ADD_FILM_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Genre getById(long id) {
        return getById(GET_BY_ID, id);
    }

    @Override
    public Collection<Genre> getAll() {
        return getAll(GET_ALL);
    }

    @Override
    public void addFilmGenre(long filmId, Set<Genre> genres) {
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);

        if (genres != null && !genres.isEmpty()) {
            jdbc.batchUpdate(ADD_FILM_GENRE, genres, genres.size(),
                    (PreparedStatement ps, Genre genre) -> {
                        ps.setLong(1, filmId);
                        ps.setLong(2, genre.getId());
                    });
        }
    }
}
