package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MPADbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MPAService {
    private final MPADbStorage mpaDbStorage;

    public Collection<MPA> getAll() {
        log.info("Запрос на получение списка рейтингов MPA");
        Collection<MPA> mpa = mpaDbStorage.getAll();
        log.info("Успешное получение списка рейтингов MPA");
        return mpa;
    }

    public MPA getById(long id) {
        log.info("Запрос на получение рейтинга MPA с id {}", id);
        MPA mpa = mpaDbStorage.getById(id);
        log.info("Успешное получение рейтинга MPA с id {}", id);
        return mpa;
    }
}
