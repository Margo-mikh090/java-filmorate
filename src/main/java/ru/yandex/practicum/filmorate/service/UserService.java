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
        Collection<User> users = userStorage.getAll();
        log.info("Успешное получение списка пользователей");
        return users;
    }

    public User getById(long id) {
        log.info("Запрос на получение пользователя с id {}", id);
        User user = userStorage.getById(id);
        log.info("Успешное получение пользователя с id {}", id);
        return user;
    }

    public void deleteById(long id) {
        log.info("Запрос на удаление пользователя с id {}", id);
        userStorage.deleteById(id);
    }

    public User create(User user) {
        log.info("Запрос на создание пользователя с данными: {}", user);
        User createdUser = userStorage.create(user);
        log.info("Успешное создание пользователя с данными: {}", createdUser);
        return createdUser;
    }

    public User update(User userToUpdate) {
        log.info("Запрос на обновление пользователя с данными: {}", userToUpdate);
        User updatedUser = userStorage.update(userToUpdate);
        log.info("Успешное обновление пользователя с данными: {}", updatedUser);
        return updatedUser;
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
        Set<User> commonFriends = fromUserFriends.stream().filter(toUserFriends::contains).collect(Collectors.toSet());
        log.info("Успешное получение списка общих друзей пользователя с id {} пользователю с id {}", fromId, toId);
        return commonFriends;
    }

    public Set<User> getUserFriends(long id) {
        log.info("Запрос на получение списка друзей пользователя с id {}", id);
        Set<User> result = new HashSet<>(friendshipDbStorage.getUserFriends(id));
        System.out.println(result);
        log.info("Успешное получение списка друзей пользователя с id {}", id);
        return result;
    }
}
