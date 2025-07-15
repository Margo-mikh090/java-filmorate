package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipDbStorage;
    private final EventService eventService;

    public Collection<User> getAll() {
        log.info("Запрос на получение списка пользователей");
        return userStorage.getAll();
    }

    public User getById(long id) {
        log.info("Запрос на получение пользователя с id {}", id);
        return userStorage.getById(id);
    }

    public void deleteById(long id) {
        log.info("Запрос на удаление пользователя с id {}", id);
        userStorage.deleteById(id);
    }

    public User create(User user) {
        log.info("Запрос на создание пользователя с данными: {}", user);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User userToUpdate) {
        log.info("Запрос на обновление пользователя с данными: {}", userToUpdate);
        if (userToUpdate.getName().isBlank()) {
            userToUpdate.setName(userToUpdate.getLogin());
        }
        return userStorage.update(userToUpdate);
    }

    public void addFriend(long fromId, long toId) {
        log.info("Запрос на добавление в друзья от пользователя с id {} пользователю с id {}", fromId, toId);
        friendshipDbStorage.addFriend(fromId, toId);
        eventService.addEvent(Event.builder()
                .userId(fromId)
                .entityId(toId)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .build());
    }

    public void removeFriend(long fromId, long toId) {
        log.info("Запрос на удаление друга от пользователя с id {} пользователю с id {}", fromId, toId);
        friendshipDbStorage.removeFriend(fromId, toId);
        eventService.addEvent(Event.builder()
                .userId(fromId)
                .entityId(toId)
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .build());
    }

    public Set<User> getMutualFriends(long fromId, long toId) {
        log.info("Запрос на получение списка общих друзей пользователя с id {} пользователю с id {}", fromId, toId);
        return new HashSet<>(friendshipDbStorage.getCommonFriends(fromId, toId));
    }

    public Set<User> getUserFriends(long id) {
        log.info("Запрос на получение списка друзей пользователя с id {}", id);
        return new HashSet<>(friendshipDbStorage.getUserFriends(id));
    }
}
