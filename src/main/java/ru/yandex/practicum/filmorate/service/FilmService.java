package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeDbStorage;

    public Collection<Film> getAll() {
        log.info("Запрос на получение списка фильмов");
        return filmStorage.getAll();
    }

    public Film getById(long id) {
        log.info("Запрос на получение фильма с id {}", id);
        return filmStorage.getById(id);
    }

    public void deleteById(long id) {
        log.info("Запрос на удаление фильма с id {}", id);
        filmStorage.deleteById(id);
    }

    public Film create(Film film) {
        log.info("Запрос на создание фильма с данными: {}", film);
        return filmStorage.create(film);
    }

    public Film update(Film filmToUpdate) {
        log.info("Запрос на обновление фильма с данными: {}", filmToUpdate);
        return filmStorage.update(filmToUpdate);
    }

    public void addLike(long filmId, long userId) {
        log.info("Запрос на добавление лайка к фильму с id {}", filmId);
        likeDbStorage.addLike(userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        log.info("Запрос на удаление лайка к фильму с id {}", filmId);
        likeDbStorage.removeLike(userId, filmId);
    }

    public List<Film> getRating(long count, Integer genreId, Integer year) {
        log.info("Запрос на топ фильмов (count: {}, жанр: {}, год: {})", count, genreId, year);

        if (count <= 0) {
            throw new ConditionsNotMetException(
                    "Кол-во фильмов должно быть положительным числом, введенное значение - " + count
            );
        }

        long limit = (genreId != null || year != null) ? Integer.MAX_VALUE : count;
        return filmStorage.getRating(limit, genreId, year).stream().toList();
    }

    public List<Film> getDirectorFilm(long directorId, String sortBy) {
        log.info("Запрос на получение списка фильмов для директора id={} с сортировкой={}", directorId, sortBy);
        return filmStorage.getDirectorFilm(directorId, sortBy).stream().toList();
    }

    public List<Film> getCommonFilms(long firstUserId, long secondUserId) {
        log.info("Запрос на получение списка общих фильмов между пользователями с id {} и {}", firstUserId, secondUserId);
        return filmStorage.getCommonFilms(firstUserId, secondUserId).stream().toList();
    }
}
