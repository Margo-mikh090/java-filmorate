package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Collection<Director> getAll() {
        log.info("Получение списка режиссеров");
        return directorStorage.getAll();
    }

    public Director getById (long id) {
        log.info("Получение режиссера по id={}", id);
        return directorStorage.getById(id);
    }

    public Director create(Director director) {
        log.info("Создание режиссера name={}", director.getName());
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        log.info("Обновление режиссера id={}", director.getId());
        return directorStorage.update(director);
    }

    public void deleteById(long id) {
        log.info("Удаление режиссера id={}", id);
        directorStorage.deleteById(id);
    }
}
