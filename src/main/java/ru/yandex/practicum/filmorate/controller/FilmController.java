package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.Marker;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Запрос на получение списка фильмов");
        return films.values();
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление фильма с параметрами {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен с параметрами {}", film);
        return film;
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public Film update(@Valid @RequestBody Film filmToUpdate) {
        log.info("Запрос на изменение фильма с параметрами {}", filmToUpdate);
        Integer id = filmToUpdate.getId();
        if (!films.containsKey(id)) {
            log.warn("Ошибка обновления фильма. Фильм не найден");
            throw new NotFoundException("Фильм с данным id не найден");
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

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
