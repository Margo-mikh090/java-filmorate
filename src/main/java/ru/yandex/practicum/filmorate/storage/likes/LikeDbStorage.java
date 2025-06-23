package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Repository
@RequiredArgsConstructor
public class LikeDbStorage {
    private final JdbcTemplate jdbc;
    private static final String ADD_LIKE = "INSERT INTO likes(user_id, film_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";

    public void addLike(long userId, long filmId) {
        try {
            jdbc.update(ADD_LIKE, userId, filmId);
        } catch (DuplicateKeyException e) {

        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Данные не найдены");
        }
    }

    public void removeLike(long userId, long filmId) {
        int deletedRecord = jdbc.update(REMOVE_LIKE, userId, filmId);
        if (deletedRecord == 0) {
            throw new NotFoundException("Данные не найдены");
        }
    }
}
