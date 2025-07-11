package ru.yandex.practicum.filmorate.storage.films;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.genres.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.Collection;

@Repository
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private final GenreStorage genreDbStorage;

    private static final String GET_ALL = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, ARRAY_AGG(DISTINCT g.genre_id) AS genres, ARRAY_AGG(DISTINCT l.user_id) AS likes " +
            "FROM films AS f " +
            "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
            "LEFT JOIN likes AS l ON f.id = l.film_id " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
            "GROUP BY f.id";

    private static final String GET_BY_ID = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, ARRAY_AGG(DISTINCT g.genre_id) AS genres, ARRAY_AGG(DISTINCT l.user_id) AS likes " +
            "FROM films AS f " +
            "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
            "LEFT JOIN likes AS l ON f.id = l.film_id " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
            "WHERE f.id = ? " +
            "GROUP BY f.id";

    private static final String DELETE_BY_ID = "DELETE FROM films WHERE id = ?";

    private static final String CREATE = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
            "mpa_id = ? WHERE id = ?";

    private static final String GET_RATING = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, ARRAY_AGG(DISTINCT g.genre_id) AS genres, ARRAY_AGG(DISTINCT l.user_id) AS likes " +
            "FROM films AS f " +
            "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
            "LEFT JOIN likes AS l ON f.id = l.film_id " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";

    private static final String GET_RATING_BY_GENRE = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, ARRAY_AGG(DISTINCT g.genre_id) AS genres, ARRAY_AGG(DISTINCT l.user_id) AS likes " +
            "FROM films AS f " +
            "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
            "LEFT JOIN likes AS l ON f.id = l.film_id " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
            "WHERE EXISTS (SELECT 1 FROM film_genres WHERE film_id = f.id AND genre_id = ?) " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";

    private static final String GET_RATING_BY_YEAR = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, ARRAY_AGG(DISTINCT g.genre_id) AS genres, ARRAY_AGG(DISTINCT l.user_id) AS likes " +
            "FROM films AS f " +
            "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
            "LEFT JOIN likes AS l ON f.id = l.film_id " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
            "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";

    private static final String GET_RATING_BY_GENRE_AND_YEAR = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, ARRAY_AGG(DISTINCT g.genre_id) AS genres, ARRAY_AGG(DISTINCT l.user_id) AS likes " +
            "FROM films AS f " +
            "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
            "LEFT JOIN likes AS l ON f.id = l.film_id " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
            "WHERE EXISTS (SELECT 1 FROM film_genres WHERE film_id = f.id AND genre_id = ?) " +
            "AND EXTRACT(YEAR FROM f.release_date) = ? " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, GenreStorage genreDbStorage) {
        super(jdbc, mapper);
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public Film create(Film film) {
        try {
            long id = create(CREATE,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());

            for (Genre genre : film.getGenres()) {
                genreDbStorage.addFilmGenre(id, genre.getId());
            }
            return getById(GET_BY_ID, id);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Данные не найдены");
        }
    }

    @Override
    public Film update(Film film) {
        update(UPDATE, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        for (Genre genre : film.getGenres()) {
            genreDbStorage.addFilmGenre(film.getId(), genre.getId());
        }
        return getById(film.getId());
    }

    @Override
    public void deleteById(long id) {
        deleteById(DELETE_BY_ID, id);
    }

    @Override
    public Collection<Film> getAll() {
        return getAll(GET_ALL);
    }

    @Override
    public Film getById(long id) {
        return getById(GET_BY_ID, id);
    }

    @Override
    public Collection<Film> getRating(long count, Integer genreId, Integer year) {
        if (genreId != null && year != null) {
            return jdbc.query(GET_RATING_BY_GENRE_AND_YEAR, mapper, genreId, year, count);
        } else if (genreId != null) {
            return jdbc.query(GET_RATING_BY_GENRE, mapper, genreId, count);
        } else if (year != null) {
            return jdbc.query(GET_RATING_BY_YEAR, mapper, year, count);
        } else {
            return jdbc.query(GET_RATING, mapper, count);
        }
    }
}
