package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
public class EventServiceTest {
    private final UserDbStorage userDbStorage;
    private final UserService userService;
    private final EventService eventService;

    @Test
    public void testGetAllEventsByUserId() {
        User user1 = new User("email@1gmail.com", "login1", "name 1", LocalDate.now());
        user1 = userDbStorage.create(user1);
        User user2 = new User("email@2gmail.com", "login2", "name 2", LocalDate.now());
        user2 = userDbStorage.create(user2);
        userService.addFriend(user1.getId(), user2.getId());

        Event actual = eventService.getUserFeed(1L).get(0);

        assertThat(actual)
                .hasFieldOrPropertyWithValue("eventId", 1L)
                .hasFieldOrPropertyWithValue("userId", 1L)
                .hasFieldOrPropertyWithValue("entityId", 2L)
                .hasFieldOrPropertyWithValue("eventType", EventType.FRIEND)
                .hasFieldOrPropertyWithValue("operation", Operation.ADD);
    }
}
