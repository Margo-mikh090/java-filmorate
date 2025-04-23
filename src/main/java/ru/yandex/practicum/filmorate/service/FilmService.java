package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    public void deleteById(int id) {
        filmStorage.deleteById(id);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film filmToUpdate) {
        return filmStorage.update(filmToUpdate);
    }

    public void addLike(int filmId, int userId) {
        log.info("Запрос на добавление лайка к фильму с id {}", filmId);
        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);
        film.addLike(user.getId());
        log.info("Лайк успешно добавлен у фильма с id {}", filmId);
    }

    public void removeLike(int filmId, int userId) {
        log.info("Запрос на удаление лайка к фильму с id {}", filmId);
        Film film = filmStorage.getById(filmId);
        User user = userStorage.getById(userId);
        film.removeLike(user.getId());
        log.info("Лайк успешно удален у фильма с id {}", filmId);
    }

    public List<Film> getRating(Integer count) {
        log.info("Запрос на получение списка {} фильмов по количеству лайков", count);
        return filmStorage.getAll().stream()
                .sorted((film1, film2) -> film2.getUserLikes().size() - film1.getUserLikes().size())
                .limit(count)
                .toList();
    }
}
