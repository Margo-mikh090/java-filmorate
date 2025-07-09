package ru.yandex.practicum.filmorate.storage.likes;

import java.util.Map;
import java.util.Set;

public interface LikeStorage {
    void addLike(long userId, long filmId);

    void removeLike(long userId, long filmId);

    Map<Long, Set<Long>> getAllUserLikes();
}
