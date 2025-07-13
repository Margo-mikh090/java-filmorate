package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.films.FilmDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbc;
    private Film film1;
    private Film film2;

    @BeforeEach
    public void beforeEach() {
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM users");
        jdbc.update("DELETE FROM film_genres");
        jdbc.update("DELETE FROM films");

        jdbc.update("INSERT INTO users (id, email, login, name, birthday) VALUES (1, 'user1@mail.com', 'login1', 'User 1', '1990-01-01')");
        jdbc.update("INSERT INTO users (id, email, login, name, birthday) VALUES (2, 'user2@mail.com', 'login2', 'User 2', '1990-01-01')");
        jdbc.update("INSERT INTO users (id, email, login, name, birthday) VALUES (3, 'user3@mail.com', 'login3', 'User 3', '1990-01-01')");

        film1 = new Film("name 1", "description 1", LocalDate.now(), 190);
        film1.setMpa(new MPA(3L, null));
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1L, null));
        genres.add(new Genre(5L, null));
        film1.setGenres(genres);
        film1 = filmDbStorage.create(film1);

        film2 = new Film("name 2", "description 2", LocalDate.now(), 190);
        film2.setMpa(new MPA(3L, null));
        film2.setGenres(genres);

        film2 = filmDbStorage.create(film2);

        jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", film1.getId(), 1L);
        jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", film1.getId(), 2L);
        jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", film2.getId(), 1L);
    }

    @Test
    public void testCreate() {
        Set<Genre> genresTest = new HashSet<>();
        genresTest.add(new Genre(1L, "Комедия"));
        genresTest.add(new Genre(5L, "Документальный"));
        assertThat(film1)
                .hasFieldOrPropertyWithValue("name", "name 1")
                .hasFieldOrPropertyWithValue("description", "description 1")
                .hasFieldOrPropertyWithValue("duration", 190)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.now())
                .hasFieldOrPropertyWithValue("mpa", new MPA(3L, "PG-13"))
                .hasFieldOrPropertyWithValue("genres", genresTest);
    }

    @Test
    public void testUpdate() {
        film1.setMpa(new MPA(1L, null));
        Film film = filmDbStorage.update(film1);
        Set<Genre> genresTest = new HashSet<>();
        genresTest.add(new Genre(1L, "Комедия"));
        genresTest.add(new Genre(5L, "Документальный"));
        assertThat(film)
                .hasFieldOrPropertyWithValue("name", "name 1")
                .hasFieldOrPropertyWithValue("description", "description 1")
                .hasFieldOrPropertyWithValue("duration", 190)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.now())
                .hasFieldOrPropertyWithValue("mpa", new MPA(1L, "G"))
                .hasFieldOrPropertyWithValue("genres", genresTest);
    }

    @Test
    public void testGetAll() {
        Collection<Film> allFilms = filmDbStorage.getAll();
        assertThat(allFilms.size()).isEqualTo(2);
    }

    @Test
    public void testGetById() {
        Film film = filmDbStorage.getById(film2.getId());
        assertThat(film).hasFieldOrPropertyWithValue("name", "name 2");
    }

    @Test
    public void testDeleteById() {
        filmDbStorage.deleteById(film1.getId());
        Collection<Film> allFilms = filmDbStorage.getAll();
        assertThat(allFilms.size()).isEqualTo(1);
    }

    @Test
    public void testGetRating_WithoutFilters() {
        Collection<Film> result = filmDbStorage.getRating(10, null, null);
        assertThat(result)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactly(film1.getId(), film2.getId());
    }

    @Test
    public void testGetRating_WithGenreFilter() {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ? AND genre_id = 1", film2.getId());
        Collection<Film> result = filmDbStorage.getRating(10, 1, null);
        assertThat(result)
                .hasSize(1)
                .extracting(Film::getId)
                .containsExactly(film1.getId());
    }

    @Test
    public void testGetRating_WithYearFilter() {
        int currentYear = LocalDate.now().getYear();
        Collection<Film> result = filmDbStorage.getRating(10, null, currentYear);
        assertThat(result)
                .hasSize(2)
                .allMatch(f -> f.getReleaseDate().getYear() == currentYear);
    }

    @Test
    public void testGetRating_WithLimit() {
        Collection<Film> result = filmDbStorage.getRating(1, null, null);
        assertThat(result)
                .hasSize(1)
                .extracting(Film::getId)
                .containsExactly(film1.getId());
    }

    @Test
    public void testGetRating_WithNonExistingGenre_ShouldReturnEmpty() {
        Collection<Film> result = filmDbStorage.getRating(10, 999, null);
        assertThat(result).isEmpty();
    }

    @Test
    public void testGetRating_WithNonExistingYear_ShouldReturnEmpty() {
        Collection<Film> result = filmDbStorage.getRating(10, null, 1800);
        assertThat(result).isEmpty();
    }

    @Test
    public void testGetRating_WithGenreAndYearFilters() {
        jdbc.update("UPDATE films SET release_date = ? WHERE id = ?",
                LocalDate.of(2000, 1, 1), film1.getId());
        jdbc.update("UPDATE films SET release_date = ? WHERE id = ?",
                LocalDate.of(2005, 1, 1), film2.getId());

        Collection<Film> result = filmDbStorage.getRating(10, 1, 2000);
        assertThat(result)
                .hasSize(1)
                .extracting(Film::getId)
                .containsExactly(film1.getId());
    }

    @Test
    public void testGetRating_OrderByLikes() {
        jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", film2.getId(), 2L);
        jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", film2.getId(), 3L);

        Collection<Film> result = filmDbStorage.getRating(10, null, null);
        assertThat(result)
                .extracting(Film::getId)
                .containsExactly(film2.getId(), film1.getId());
    }
}
