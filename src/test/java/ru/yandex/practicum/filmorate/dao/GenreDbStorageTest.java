package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genres.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class})
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    public void testGetAll() {
        Collection<Genre> allGenres = genreDbStorage.getAll();
        assertThat(allGenres.size()).isEqualTo(6);
    }

    @Test
    public void testGetById() {
        Genre genre = genreDbStorage.getById(3L);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Мультфильм");
    }
}
