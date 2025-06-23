package ru.yandex.practicum.filmorate.storage.genres;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> {

    private static final String GET_BY_ID = "SELECT * FROM genres WHERE id = ?";
    private static final String GET_ALL = "SELECT * FROM genres";
    private static final String ADD_FILM_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Genre getById(long id) {
        return getById(GET_BY_ID, id);
    }

    public Collection<Genre> getAll() {
        return getAll(GET_ALL);
    }

    public void addFilmGenre(long filmId, long genreId) {
        try {
            jdbc.update(ADD_FILM_GENRE, filmId, genreId);
        } catch (DuplicateKeyException e) {
            return;
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Данные не найдены");
        }
    }
}
