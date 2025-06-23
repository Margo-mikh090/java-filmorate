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
        log.info("Запрос на получение списка фильмов");
        Collection<Film> films = filmStorage.getAll();
        log.info("Успешное получение списка фильмов");
        return films;
    }

    public Film getById(long id) {
        log.info("Запрос на получение фильма с id {}", id);
        Film film = filmStorage.getById(id);
        log.info("Успешное получение фильма с id {}", id);
        return film;
    }

    public void deleteById(long id) {
        log.info("Запрос на удаление фильма с id {}", id);
        filmStorage.deleteById(id);
    }

    public Film create(Film film) {
        log.info("Запрос на создание фильма с данными: {}", film);
        Film createdFilm = filmStorage.create(film);
        log.info("Успешное создание фильма с данными: {}", createdFilm);
        return createdFilm;
    }

    public Film update(Film filmToUpdate) {
        log.info("Запрос на обновление фильма с данными: {}", filmToUpdate);
        Film updatedFilm = filmStorage.update(filmToUpdate);
        log.info("Успешное обновление фильма с данными: {}", updatedFilm);
        return updatedFilm;
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
        List<Film> rate = filmStorage.getRating(count).stream().toList();
        log.info("Успешное получение списка {} фильмов по количеству лайков", count);
        return rate;
    }
}
