package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getAll() {
        log.info("Запрос на получение списка жанров");
        Collection<Genre> genres = genreService.getAll();
        log.info("Успешное получение списка жанров");
        return genres;
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable long id) {
        log.info("Запрос на получение жанра с id {}", id);
        Genre genre = genreService.getById(id);
        log.info("Успешное получение жанра с id {}", id);
        return genre;
    }
}
