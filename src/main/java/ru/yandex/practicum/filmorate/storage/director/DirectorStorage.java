package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface DirectorStorage {
    Collection<Director> getAll();

    Director getById(long id);

    void deleteById(long id);

    Director create(Director director);

    Director update(Director director);

    void saveFilmDirectors(long filmId, Set<Director> directors);

    Set<Director> getByFilmId(long filmId);

    Map<Long, Set<Director>> findAllIndexByFilmId();
}
