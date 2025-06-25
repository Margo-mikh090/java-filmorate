package ru.yandex.practicum.filmorate.storage.likes;

public interface LikeStorage {
    void addLike(long userId, long filmId);

    void removeLike(long userId, long filmId);
}
