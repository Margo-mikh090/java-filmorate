package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
public class FriendshipDbStorageTest {
    private final FriendshipDbStorage friendshipDbStorage;
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbc;
    private User user1;
    private User user2;

    @BeforeEach
    public void beforeEach() {
        jdbc.update("DELETE FROM users");
        jdbc.update("DELETE FROM friendship");

        user1 = new User("email@1gmail.com", "login1", "name 1", LocalDate.now());
        user1 = userDbStorage.create(user1);

        user2 = new User("email@2gmail.com", "login2", "name 2", LocalDate.now());
        user2 = userDbStorage.create(user2);
    }

    @Test
    public void testAddFriend() {
        friendshipDbStorage.addFriend(user1.getId(), user2.getId());
        int rows = jdbc.queryForObject("SELECT COUNT(*) FROM friendship", Integer.class);
        assertThat(rows).isEqualTo(1);
        friendshipDbStorage.addFriend(user2.getId(), user1.getId());
        rows = jdbc.queryForObject("SELECT COUNT(*) FROM friendship", Integer.class);
        assertThat(rows).isEqualTo(2);
    }

    @Test
    public void testRemoveFriend() {
        friendshipDbStorage.addFriend(user1.getId(), user2.getId());
        friendshipDbStorage.addFriend(user2.getId(), user1.getId());
        friendshipDbStorage.removeFriend(user1.getId(), user2.getId());
        assertThrows(NotFoundException.class, () -> friendshipDbStorage.removeFriend(50L, user2.getId()));
        int rows = jdbc.queryForObject("SELECT COUNT(*) FROM friendship", Integer.class);
        assertThat(rows).isEqualTo(1);
    }

    @Test
    public void testGetUserFriends() {
        friendshipDbStorage.addFriend(user1.getId(), user2.getId());
        Collection<User> userFriends1 = friendshipDbStorage.getUserFriends(user1.getId());
        assertThat(userFriends1.size()).isEqualTo(1);
        Collection<User> userFriends2 = friendshipDbStorage.getUserFriends(user2.getId());
        assertThat(userFriends2.size()).isEqualTo(0);
        assertThrows(NotFoundException.class, () -> friendshipDbStorage.getUserFriends(50));
    }
}
