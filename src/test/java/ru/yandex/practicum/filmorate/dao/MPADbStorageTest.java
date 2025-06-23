package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mappers.MPARowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MPADbStorage;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MPADbStorage.class, MPARowMapper.class})
public class MPADbStorageTest {
    private final MPADbStorage mpaDbStorage;

    @Test
    public void testGetAll() {
        Collection<MPA> allMpa = mpaDbStorage.getAll();
        assertThat(allMpa.size()).isEqualTo(5);
    }

    @Test
    public void testGetById() {
        MPA mpa = mpaDbStorage.getById(1);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }
}
