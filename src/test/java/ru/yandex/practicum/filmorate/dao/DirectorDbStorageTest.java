package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
public class DirectorDbStorageTest {
    private final DirectorDbStorage directorDbStorage;
    private final JdbcTemplate jdbc;
    private Director director;

    @BeforeEach
    public void beforeEach() {
        jdbc.update("DELETE FROM director");

        director = new Director();
        director.setName("Testov");

        director = directorDbStorage.create(director);
    }

    @Test
    public void testCreate() {
        assertThat(director).hasFieldOrPropertyWithValue("name", "Testov");
    }

    @Test
    public void testUpdate() {
        director.setName("Spilbergov");
        director = directorDbStorage.update(director);

        Director dirFromDb = directorDbStorage.getById(director.getId());
        assertThat(dirFromDb).hasFieldOrPropertyWithValue("name", "Spilbergov");
    }

    @Test
    public void testGetAll() {
        Collection<Director> allFilms = directorDbStorage.getAll();
        assertThat(allFilms.size()).isEqualTo(1);
    }

    @Test
    public void testGetById() {
        Director dirFromDb = directorDbStorage.getById(director.getId());
        assertThat(dirFromDb).hasFieldOrPropertyWithValue("name", "Testov");
    }

    @Test
    public void testDeleteById() {
        directorDbStorage.deleteById(director.getId());
        Collection<Director> allFilms = directorDbStorage.getAll();
        assertThat(allFilms.size()).isEqualTo(0);
    }
}
