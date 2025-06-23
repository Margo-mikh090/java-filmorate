package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MPAController {
    private final MPAService mpaService;

    @GetMapping
    public Collection<MPA> getAll() {
        return mpaService.getAll();
    }

    @GetMapping("/{id}")
    public MPA getById(@PathVariable long id) {
        return mpaService.getById(id);
    }
}
