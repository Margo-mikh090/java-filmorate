package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MPAStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MPAService {
    private final MPAStorage mpaDbStorage;

    public Collection<MPA> getAll() {
        log.info("Запрос на получение списка рейтингов MPA");
        return mpaDbStorage.getAll();
    }

    public MPA getById(long id) {
        log.info("Запрос на получение рейтинга MPA с id {}", id);
        return mpaDbStorage.getById(id);
    }
}
