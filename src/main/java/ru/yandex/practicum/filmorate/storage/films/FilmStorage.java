package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAll();

    Collection<Film> getByList(List<Long> listFilms);

    Film getById(long id);

    void deleteById(long id);

    Film create(Film film);

    Film update(Film film);

    Collection<Film> getRating(long count, Integer genreId, Integer year);

    Collection<Film> getDirectorFilm(long directorId, String sortBy);

    Collection<Film> getCommonFilms(long firstUserId, long secondUserId);

    Collection<Film> searchFilms(String query, boolean searchByTitle, boolean searchByDirector);
}
