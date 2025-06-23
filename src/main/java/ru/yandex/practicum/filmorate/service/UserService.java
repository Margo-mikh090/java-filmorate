package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userStorage;
    private final FriendshipDbStorage friendshipDbStorage;

    public Collection<User> getAll() {
        log.info("Запрос на получение списка пользователей");
        return userStorage.getAll();
    }

    public User getById(long id) {
        User user = userStorage.getById(id);
        System.out.println(user);
        return userStorage.getById(id);
    }

    public void deleteById(long id) {
        userStorage.deleteById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User userToUpdate) {
        return userStorage.update(userToUpdate);
    }

    public void addFriend(long fromId, long toId) {
        log.info("Запрос на добавление в друзья от пользователя с id {} пользователю с id {}", fromId, toId);
        friendshipDbStorage.addFriend(fromId, toId);
        log.info("Успешное добавления в друзья пользователей с id {} и {}", fromId, toId);
    }

    public void removeFriend(long fromId, long toId) {
        log.info("Запрос на удаление друга от пользователя с id {} пользователю с id {}", fromId, toId);
        friendshipDbStorage.removeFriend(fromId, toId);
        log.info("Успешное удаление друга пользователей с id {} и {}", fromId, toId);
    }

    public Set<User> getMutualFriends(long fromId, long toId) {
        log.info("Запрос на получение списка общих друзей пользователя с id {} пользователю с id {}", fromId, toId);
        Set<User> fromUserFriends = getUserFriends(fromId);
        Set<User> toUserFriends = getUserFriends(toId);
        return fromUserFriends.stream().filter(toUserFriends::contains).collect(Collectors.toSet());
    }

    public Set<User> getUserFriends(long id) {
        log.info("Запрос на получение списка друзей пользователя с id {}", id);
        Set<User> result = new HashSet<>(friendshipDbStorage.getUserFriends(id));
        System.out.println(result);
        log.info("Успешное получение списка друзей пользователя с id {}", id);
        return result;
    }
}
