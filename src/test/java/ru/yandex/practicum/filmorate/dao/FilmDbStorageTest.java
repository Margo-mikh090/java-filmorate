package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.films.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genres.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreDbStorage.class, GenreRowMapper.class})
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbc;
    private Film film1;
    private Film film2;

    @BeforeEach
    public void beforeEach() {
        jdbc.update("DELETE FROM films");

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
}
