package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genres.GenreDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public Genre getById(long id) {
        log.info("Запрос на получение жанра с id {}", id);
        Genre genre = genreDbStorage.getById(id);
        log.info("Успешное получение жанра с id {}", id);
        return genre;
    }

    public Collection<Genre> getAll() {
        log.info("Запрос на получение списка жанров");
        Collection<Genre> genres = genreDbStorage.getAll();
        log.info("Успешное получение списка жанров");
        return genres;
    }
}
