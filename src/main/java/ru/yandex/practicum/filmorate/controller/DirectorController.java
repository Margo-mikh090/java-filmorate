package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.annotation.Marker;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getAll() {
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable long id) {
        return directorService.getById(id);
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public Director create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public Director update(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        directorService.deleteById(id);
    }
}
