package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public void deleteById(int id) {
        userStorage.deleteById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User userToUpdate) {
        return userStorage.update(userToUpdate);
    }

    public void addFriend(int fromId, int toId) {
        log.info("Запрос на добавление в друзья от пользователя с id {} пользователю с id {}", fromId, toId);
        User fromUser = userStorage.getById(fromId);
        User toUser = userStorage.getById(toId);
        fromUser.getFriends().add(toId);
        toUser.getFriends().add(fromId);
        log.info("Успешное добавления в друзья пользователей с id {} и {}", fromId, toId);
    }

    public void removeFriend(int fromId, int toId) {
        log.info("Запрос на удаление друга от пользователя с id {} пользователю с id {}", fromId, toId);
        User fromUser = userStorage.getById(fromId);
        User toUser = userStorage.getById(toId);
        fromUser.getFriends().remove(toId);
        toUser.getFriends().remove(fromId);
        log.info("Успешное удаление друга пользователей с id {} и {}", fromId, toId);
    }

    public Set<User> getMutualFriends(int fromId, int toId) {
        log.info("Запрос на получение списка общих друзей пользователя с id {} пользователю с id {}", fromId, toId);
        Set<User> fromUserFriends = getUserFriends(fromId);
        Set<User> toUserFriends = getUserFriends(toId);
        return fromUserFriends.stream().filter(toUserFriends::contains).collect(Collectors.toSet());
    }

    public Set<User> getUserFriends(int id) {
        log.info("Запрос на получение списка друзей пользователя с id {}", id);
        User user = userStorage.getById(id);
        return user.getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toSet());
    }
}
