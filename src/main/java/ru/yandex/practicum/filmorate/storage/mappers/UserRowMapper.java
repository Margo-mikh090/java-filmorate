package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User(rs.getString("email"), rs.getString("login"),
                rs.getString("name"), rs.getDate("birthday").toLocalDate());
        user.setId(rs.getLong("id"));
        user.setFriends(getLongSet(rs.getArray("friends_id")));

        return user;
    }

    private Set<Long> getLongSet(Array sqlArray) throws SQLException {
        if (sqlArray == null) {
            return new HashSet<>();
        }
        Object[] array = (Object[]) sqlArray.getArray();
        return Arrays.stream(array)
                .filter(Objects::nonNull)
                .map(o -> ((Number) o).longValue())
                .collect(Collectors.toSet());
    }
}
