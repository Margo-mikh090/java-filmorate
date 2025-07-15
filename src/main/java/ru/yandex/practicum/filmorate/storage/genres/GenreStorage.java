package ru.yandex.practicum.filmorate.storage.genres;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface GenreStorage {
    Genre getById(long id);

    Collection<Genre> getAll();

    void addFilmGenre(long filmId, Set<Genre> genres);
}
