package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.genres.GenreDbStorage;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final GenreDbStorage genreDbStorage;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(rs.getString("name"), rs.getString("description"),
                rs.getDate("release_date").toLocalDate(), rs.getInt("duration"));
        film.setId(rs.getLong("id"));
        film.setMpa(new MPA(rs.getLong("mpa_id"), rs.getString("mpa_name")));
        film.setUserLikes(getLongSet(rs.getArray("likes")));
        Set<Long> genresId = getLongSet(rs.getArray("genres"));
        Set<Genre> genres = genresId.stream().map(genreDbStorage::getById).collect(Collectors.toCollection(LinkedHashSet::new));
        film.setGenres(genres);

        return film;
    }

    private Set<Long> getLongSet(Array sqlArray) throws SQLException {
        if (sqlArray == null) {
            return new HashSet<>();
        }
        Object[] array = (Object[]) sqlArray.getArray();
        return Arrays.stream(array)
                .filter(Objects::nonNull)
                .map(o -> ((Number) o).longValue())
                .collect(Collectors.toSet());
    }
}
