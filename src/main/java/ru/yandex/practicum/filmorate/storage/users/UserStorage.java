package ru.yandex.practicum.filmorate.storage.users;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();

    User getById(long id);

    void deleteById(long id);

    User create(User user);

    User update(User user);
}
