package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbc;
    private static final String ADD_LIKE = "INSERT INTO likes(user_id, film_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String GET_ALL = "SELECT user_id, film_id FROM likes";

    @Override
    public void addLike(long userId, long filmId) {
        try {
            jdbc.update(ADD_LIKE, userId, filmId);
        } catch (DuplicateKeyException e) {
            log.info("Исключение DuplicateKeyException: {}", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Данные не найдены");
        }
    }

    @Override
    public void removeLike(long userId, long filmId) {
        int deletedRecord = jdbc.update(REMOVE_LIKE, userId, filmId);
        if (deletedRecord == 0) {
            throw new NotFoundException("Данные не найдены");
        }
    }

    @Override
    public Map<Long, Set<Long>> getAllUserLikes() {
        return jdbc.query(GET_ALL, rs -> {
            Map<Long, Set<Long>> userLikesMap = new HashMap<>();
            while (rs.next()) {
                long userId = rs.getLong("user_id");
                long filmId = rs.getLong("film_id");

                userLikesMap.computeIfAbsent(userId, k -> new HashSet<>()).add(filmId);
            }
            return userLikesMap;
        });
    }
}
