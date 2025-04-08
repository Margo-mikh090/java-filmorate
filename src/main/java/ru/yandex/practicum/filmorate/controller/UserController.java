package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        log.info("Запрос на получение списка пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Запрос на добавление пользователя с параметрами {}", user);
        user.setId(getNextId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен с параметрами {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User userToUpdate) {
        log.info("Запрос на изменение пользователя с параметрами {}", userToUpdate);
        Integer id = userToUpdate.getId();
        if (id == null) {
            log.warn("Ошибка обновления пользователя. Не указан id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(id)) {
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
        log.warn("Ошибка обновления пользователя. Пользователь не найден");
        throw new NotFoundException("Пользователь с данным id не найден");
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
