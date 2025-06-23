package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.Collection;

@Repository
public class FriendshipDbStorage extends BaseDbStorage<User> {
    private static final String ADD_FRIEND = "INSERT INTO friendship(first_user_id, second_user_id) VALUES (?, ?)";

    private static final String REMOVE_FRIEND = "DELETE FROM friendship WHERE first_user_id = ? AND second_user_id = ?";

    private static final String GET_USER_FRIENDS = "SELECT u.id, u.email, u.login, u.name, u.birthday, ARRAY_AGG(DISTINCT f2.second_user_id) AS friends_id " +
            "FROM users AS u " +
            "LEFT JOIN friendship AS f1 ON u.id = f1.second_user_id " +
            "LEFT JOIN friendship AS f2 ON f1.second_user_id = f2.first_user_id " +
            "WHERE f1.first_user_id = ? " +
            "GROUP BY u.id";

    private static final String USER_EXISTS = "SELECT COUNT(1) FROM users WHERE id = ?";

    public FriendshipDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    public void addFriend(long firstUserId, long secondUserId) {
        try {
            jdbc.update(ADD_FRIEND, firstUserId, secondUserId);
        } catch (DuplicateKeyException e) {

        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Данные не найдены");
        }
    }

    public void removeFriend(long firstUserId, long secondUserId) {
        if (!isUserExists(firstUserId) || !isUserExists(secondUserId)) {
            throw new NotFoundException("Данные не найдены");
        }
        jdbc.update(REMOVE_FRIEND, firstUserId, secondUserId);
    }

    public Collection<User> getUserFriends(long id) {
        if (!isUserExists(id)) {
            throw new NotFoundException("Данные не найдены");
        }
        return jdbc.query(GET_USER_FRIENDS, mapper, id);
    }

    private boolean isUserExists(long id) {
        return jdbc.queryForObject(USER_EXISTS, Long.class, id) > 0;
    }
}
