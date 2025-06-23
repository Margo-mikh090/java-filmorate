package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MPADbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MPAService {
    private final MPADbStorage mpaDbStorage;

    public Collection<MPA> getAll() {
        return mpaDbStorage.getAll();
    }

    public MPA getById(long id) {
        return mpaDbStorage.getById(id);
    }
}
