package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbc;
    private User user1 = new User();
    private User user2 = new User();

    @BeforeEach
    public void beforeEach() {
        jdbc.update("DELETE FROM users");

        user1.setName("name 1");
        user1.setBirthday(LocalDate.now());
        user1.setLogin("login1");
        user1.setEmail("email@1gmail.com");
        user1 = userDbStorage.create(user1);

        user2.setName("name 2");
        user2.setBirthday(LocalDate.now());
        user2.setLogin("login2");
        user2.setEmail("email@2gmail.com");
        user2 = userDbStorage.create(user2);
    }

    @Test
    public void testCreate() {
        assertThat(user1)
                .hasFieldOrPropertyWithValue("name", "name 1")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.now())
                .hasFieldOrPropertyWithValue("login", "login1")
                .hasFieldOrPropertyWithValue("email", "email@1gmail.com");
    }

    @Test
    public void testUpdate() {
        user1.setEmail("email@1gmail.internet");
        assertThat(user1)
                .hasFieldOrPropertyWithValue("name", "name 1")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.now())
                .hasFieldOrPropertyWithValue("login", "login1")
                .hasFieldOrPropertyWithValue("email", "email@1gmail.internet");
    }

    @Test
    public void testGetAll() {
        Collection<User> allUsers = userDbStorage.getAll();
        assertThat(allUsers.size()).isEqualTo(2);
    }

    @Test
    public void testGetById() {
        User user = userDbStorage.getById(user2.getId());
        assertThat(user).hasFieldOrPropertyWithValue("email", "email@2gmail.com");
    }

    @Test
    public void testDeleteById() {
        userDbStorage.deleteById(user2.getId());
        Collection<User> allUsers = userDbStorage.getAll();
        assertThat(allUsers.size()).isEqualTo(1);
    }
}
