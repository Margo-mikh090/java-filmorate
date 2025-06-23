package ru.yandex.practicum.filmorate.storage.users;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.Collection;

@Repository
@Primary
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String GET_ALL = "SELECT u.id, u.email, u.login, u.name, u.birthday, ARRAY_AGG(DISTINCT f.second_user_id) AS friends_id " +
            "FROM users AS u " +
            "LEFT JOIN friendship AS f ON u.id = f.first_user_id " +
            "GROUP BY u.id";

    private static final String GET_BY_ID = "SELECT u.id, u.email, u.login, u.name, u.birthday, ARRAY_AGG(DISTINCT f.second_user_id) AS friends_id " +
            "FROM users AS u " +
            "LEFT JOIN friendship AS f ON u.id = f.first_user_id " +
            "WHERE u.id = ? " +
            "GROUP BY u.id";

    private static final String DELETE_BY_ID = "DELETE FROM users WHERE id = ?";

    private static final String CREATE = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getAll() {
        return getAll(GET_ALL);
    }

    @Override
    public User getById(long id) {
        return getById(GET_BY_ID, id);
    }

    @Override
    public void deleteById(long id) {
        deleteById(DELETE_BY_ID, id);
    }

    @Override
    public User create(User user) {
        long id = create(CREATE,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(UPDATE,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }
}
