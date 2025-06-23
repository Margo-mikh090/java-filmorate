package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.films.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeDbStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final LikeDbStorage likeDbStorage;


    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(long id) {
        return filmStorage.getById(id);
    }

    public void deleteById(long id) {
        filmStorage.deleteById(id);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film filmToUpdate) {
        return filmStorage.update(filmToUpdate);
    }

    public void addLike(long filmId, long userId) {
        log.info("Запрос на добавление лайка к фильму с id {}", filmId);
        likeDbStorage.addLike(userId, filmId);
        log.info("Лайк успешно добавлен у фильма с id {}", filmId);
    }

    public void removeLike(long filmId, long userId) {
        log.info("Запрос на удаление лайка к фильму с id {}", filmId);
        likeDbStorage.removeLike(userId, filmId);
        log.info("Лайк успешно удален у фильма с id {}", filmId);
    }

    public List<Film> getRating(Integer count) {
        log.info("Запрос на получение списка {} фильмов по количеству лайков", count);
        return filmStorage.getRating(count).stream().toList();
    }
}
