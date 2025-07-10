package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.films.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genres.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeDbStorage.class, FilmDbStorage.class, FilmRowMapper.class, GenreDbStorage.class, GenreRowMapper.class, UserDbStorage.class, UserRowMapper.class, DirectorDbStorage.class, DirectorRowMapper.class})
public class LikeDbStorageTest {
    private final LikeDbStorage likeDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbc;
    private User user;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        jdbc.update("DELETE FROM likes");

        user = new User("email@gmail.com", "login", "name", LocalDate.now());
        user = userDbStorage.create(user);

        film = new Film("name", "description", LocalDate.now(), 190);
        film.setMpa(new MPA(3L, null));
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1L, null));
        genres.add(new Genre(5L, null));
        film.setGenres(genres);
        film = filmDbStorage.create(film);
    }

    @Test
    public void testAddLike() {
        likeDbStorage.addLike(user.getId(), film.getId());
        int rows = jdbc.queryForObject("SELECT COUNT(*) FROM likes", Integer.class);
        assertThat(rows).isEqualTo(1);
        likeDbStorage.addLike(user.getId(), film.getId());
        rows = jdbc.queryForObject("SELECT COUNT(*) FROM likes", Integer.class);
        assertThat(rows).isEqualTo(1);
    }

    @Test
    public void testRemoveLike() {
        likeDbStorage.addLike(user.getId(), film.getId());
        likeDbStorage.removeLike(user.getId(), film.getId());
        int rows = jdbc.queryForObject("SELECT COUNT(*) FROM likes", Integer.class);
        assertThat(rows).isEqualTo(0);
        assertThrows(NotFoundException.class, () -> likeDbStorage.removeLike(user.getId(), film.getId()));
    }

    @Test
    public void testGetRating() {
        User userRate = new User("email@gmail.ru", "newlogin", "name", LocalDate.now());
        userRate = userDbStorage.create(userRate);

        Film filmRate = new Film("name", "description", LocalDate.now(), 190);
        filmRate.setMpa(new MPA(3L, null));
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1L, null));
        genres.add(new Genre(5L, null));
        filmRate.setGenres(genres);
        filmRate = filmDbStorage.create(filmRate);

        likeDbStorage.addLike(user.getId(), film.getId());
        likeDbStorage.addLike(userRate.getId(), film.getId());
        likeDbStorage.addLike(userRate.getId(), filmRate.getId());

        int rows = jdbc.queryForObject("SELECT COUNT(*) FROM likes", Integer.class);
        assertThat(rows).isEqualTo(3);

        List<Film> filmsRate = (List<Film>) filmDbStorage.getRating(2);
        assertThat(filmsRate.size()).isEqualTo(2);

        assertThat(filmsRate.getFirst().getId()).isEqualTo(film.getId());
    }
}
