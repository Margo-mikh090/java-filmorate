package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genres.GenreStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreDbStorage;

    public Genre getById(long id) {
        log.info("Запрос на получение жанра с id {}", id);
        return genreDbStorage.getById(id);
    }

    public Collection<Genre> getAll() {
        log.info("Запрос на получение списка жанров");
        return genreDbStorage.getAll();
    }
}
