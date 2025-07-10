package ru.yandex.practicum.filmorate.storage.films;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        log.info("Запрос на получение списка фильмов");
        return films.values();
    }

    @Override
    public Film getById(long id) {
        log.info("Запрос на получение фильма с id {}", id);
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return films.get(id);
    }

    @Override
    public void deleteById(long id) {
        log.info("Запрос на удаление фильма с id {}", id);
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        films.remove(id);
    }

    @Override
    public Film create(Film film) {
        log.info("Запрос на добавление фильма с параметрами {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен с параметрами {}", film);
        return film;
    }

    @Override
    public Film update(Film filmToUpdate) {
        log.info("Запрос на изменение фильма с параметрами {}", filmToUpdate);
        Long id = filmToUpdate.getId();
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        Film oldFilm = films.get(id);
        oldFilm.setName(filmToUpdate.getName());
        if (filmToUpdate.getDescription() != null) {
            oldFilm.setDescription(filmToUpdate.getDescription());
        }
        if (filmToUpdate.getReleaseDate() != null) {
            oldFilm.setReleaseDate(filmToUpdate.getReleaseDate());
        }
        if (filmToUpdate.getDuration() != null) {
            oldFilm.setDuration(filmToUpdate.getDuration());
        }
        log.info("Фильм успешно обновлен с параметрами {}", oldFilm);
        return oldFilm;
    }

    @Override
    public Collection<Film> getRating(long count) {
        return List.of();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Collection<Film> getDirectorFilm(long directorId, String sortBy) {
        return List.of();
    }
}
