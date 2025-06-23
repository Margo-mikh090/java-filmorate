package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MPAController {
    private final MPAService mpaService;

    @GetMapping
    public Collection<MPA> getAll() {
        log.info("Запрос на получение списка рейтингов MPA");
        Collection<MPA> mpa = mpaService.getAll();
        log.info("Успешное получение списка рейтингов MPA");
        return mpa;
    }

    @GetMapping("/{id}")
    public MPA getById(@PathVariable long id) {
        log.info("Запрос на получение рейтинга MPA с id {}", id);
        MPA mpa = mpaService.getById(id);
        log.info("Успешное получение рейтинга MPA с id {}", id);
        return mpa;
    }
}
