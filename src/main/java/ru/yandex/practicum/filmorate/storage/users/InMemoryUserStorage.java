package ru.yandex.practicum.filmorate.storage.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        log.info("Запрос на получение списка пользователей");
        return users.values();
    }

    @Override
    public User getById(long id) {
        log.info("Запрос на получение пользователя с id {}", id);
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public void deleteById(long id) {
        log.info("Запрос на удаление пользователя с id {}", id);
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        users.remove(id);
    }

    @Override
    public User create(User user) {
        log.info("Запрос на добавление пользователя с параметрами {}", user);
        user.setId(getNextId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен с параметрами {}", user);
        return user;
    }

    @Override
    public User update(User userToUpdate) {
        log.info("Запрос на изменение пользователя с параметрами {}", userToUpdate);
        Long id = userToUpdate.getId();
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        User oldUser = users.get(id);
        oldUser.setEmail(userToUpdate.getEmail());
        oldUser.setLogin(userToUpdate.getLogin());
        if (userToUpdate.getName() == null) {
            oldUser.setName(userToUpdate.getLogin());
        } else {
            oldUser.setName(userToUpdate.getName());
        }
        oldUser.setBirthday(userToUpdate.getBirthday());
        log.info("Пользователь успешно обновлен с параметрами {}", oldUser);
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
