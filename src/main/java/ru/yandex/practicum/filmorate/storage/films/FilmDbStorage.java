package ru.yandex.practicum.filmorate.storage.films;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genres.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

@Repository
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private final GenreStorage genreDbStorage;
    private final DirectorStorage directorStorage;

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

    private static final String GET_RATING = """
            SELECT f.id, f.name, f.description, f.release_date, f.duration,
                   f.mpa_id, m.name AS mpa_name,
            ARRAY_AGG(DISTINCT g.genre_id) AS genres,
            ARRAY_AGG(DISTINCT l.user_id) AS likes
            FROM films AS f
            LEFT JOIN film_genres AS g ON f.id = g.film_id
            LEFT JOIN likes AS l ON f.id = l.film_id
            LEFT JOIN mpa AS m ON f.mpa_id = m.id
            WHERE (? IS NULL OR EXISTS (
                SELECT 1 FROM film_genres WHERE film_id = f.id AND genre_id = ?
            ))
            AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?)
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """;

    private static final String FILM_DIRECTOR =
            """
                    SELECT f.id, f.name, f.description, f.release_date, f.duration,
                           f.mpa_id, m.name AS mpa_name, ARRAY_AGG(DISTINCT g.genre_id) AS genres, ARRAY_AGG(DISTINCT l.user_id) AS likes
                      FROM films f
                           JOIN film_director fd
                             ON fd.film_id = f.id
                           LEFT JOIN (SELECT l.film_id,
                                             count(*) likes_count
                                        FROM likes l
                                       GROUP BY l.film_id) fl_count
                                  ON fl_count.film_id = f.id
                           LEFT JOIN film_genres AS g ON f.id = g.film_id
                           LEFT JOIN likes AS l ON f.id = l.film_id
                           LEFT JOIN mpa AS m ON f.mpa_id = m.id
                     WHERE fd.director_id = ?
                     GROUP BY f.id
                     ORDER BY DECODE(lower(?), 'year', EXTRACT(YEAR FROM f.release_date)) NULLS LAST,
                              DECODE(lower(?), 'likes', fl_count.likes_count) DESC NULLS LAST,
                              f.id
                    """;

    private static final String USER_EXISTS = "SELECT COUNT(1) FROM users WHERE id = ?";

    private static final String GET_COMMON_FILMS = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, ARRAY_AGG(DISTINCT g.genre_id) AS genres, ARRAY_AGG(DISTINCT l.user_id) AS likes " +
            "FROM films AS f " +
            "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
            "LEFT JOIN likes AS l ON f.id = l.film_id " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
            "WHERE l.film_id IN (" +
            "SELECT l1.film_id " +
            "FROM LIKES AS l1 " +
            "JOIN LIKES AS l2 ON l1.film_id = l2.film_id " +
            "WHERE l1.user_id = ? AND l2.user_id = ?) " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(l.user_id) DESC";

    public static final String BASE_FILM_QUERY = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.mpa_id, m.name AS mpa_name, ARRAY_AGG(DISTINCT g.genre_id) AS genres, ARRAY_AGG(DISTINCT l.user_id) AS likes " +
            "FROM films AS f " +
            "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
            "LEFT JOIN likes AS l ON f.id = l.film_id " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id ";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, GenreStorage genreDbStorage, DirectorStorage directorStorage) {
        super(jdbc, mapper);
        this.genreDbStorage = genreDbStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public Collection<Film> getAll() {
        final Collection<Film> films = getAll(GET_ALL);
        return addDirectorsToCollection(films);
    }

    @Override
    public Film getById(long id) {
        final Film film = getById(GET_BY_ID, id);
        film.setDirectors(directorStorage.getByFilmId(id));
        return film;
    }

    @Override
    public void deleteById(long id) {
        deleteById(DELETE_BY_ID, id);
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

            directorStorage.saveFilmDirectors(id, film.getDirectors());

            return getById(id);
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

        directorStorage.saveFilmDirectors(film.getId(), film.getDirectors());

        return getById(film.getId());
    }

    @Override
    public Collection<Film> getRating(long count, Integer genreId, Integer year) {
        return addDirectorsToCollection(
                jdbc.query(GET_RATING, mapper, genreId, genreId, year, year, count)
        );
    }

    @Override
    public Collection<Film> getDirectorFilm(long directorId, String sortBy) {
        final Collection<Film> films = jdbc.query(FILM_DIRECTOR, mapper, directorId, sortBy, sortBy);
        return addDirectorsToCollection(films);
    }

    @Override
    public Collection<Film> getCommonFilms(long firstUserId, long secondUserId) {
        if (!isUserExists(firstUserId) || !isUserExists(secondUserId)) {
            throw new NotFoundException("Данные не найдены");
        }
        final Collection<Film> films = jdbc.query(GET_COMMON_FILMS, mapper, firstUserId, secondUserId);
        return addDirectorsToCollection(films);
    }

    @Override
    public Collection<Film> searchFilms(String query, boolean searchByTitle, boolean searchByDirector) {
        StringBuilder search_film_query = new StringBuilder(BASE_FILM_QUERY);
        List<String> searchQuery = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (searchByDirector) {
            search_film_query.append("LEFT JOIN film_director AS fd ON f.id = fd.film_id ");
            search_film_query.append("LEFT JOIN director AS d ON fd.director_id = d.id ");
            conditions.add("LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%')) ");
            searchQuery.add(query);
        }
        if (searchByTitle) {
            conditions.add("LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%')) ");
            searchQuery.add(query);
        }
        if (conditions.isEmpty()) {
            throw new ConditionsNotMetException("Поиск возможен только по параметрам title и director");
        }

        search_film_query.append("WHERE ").append(String.join(" OR ", conditions));
        search_film_query.append("GROUP BY f.id ORDER BY COUNT(l.user_id) DESC");
        Collection<Film> films = jdbc.query(search_film_query.toString(), mapper, searchQuery.toArray());
        return addDirectorsToCollection(films);
    }

    private boolean isUserExists(long id) {
        return jdbc.queryForObject(USER_EXISTS, Long.class, id) > 0;
    }

    private Collection<Film> addDirectorsToCollection(Collection<Film> films) {
        final Map<Long, Set<Director>> directorIndexByFilm = directorStorage.findAllIndexByFilmId();
        films.forEach(f -> f.setDirectors(directorIndexByFilm.getOrDefault(f.getId(), new HashSet<>())));
        return films;
    }
}
