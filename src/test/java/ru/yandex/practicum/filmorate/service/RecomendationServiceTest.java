package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.films.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genres.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeDbStorage.class, FilmDbStorage.class, FilmRowMapper.class, GenreDbStorage.class, GenreRowMapper.class,
        UserDbStorage.class, UserRowMapper.class, RecommendationService.class,
        DirectorDbStorage.class, DirectorRowMapper.class})
public class RecomendationServiceTest {
    private final LikeDbStorage likeDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final RecommendationService recomendationService;

    @BeforeEach
    public void beforeEach() {
        User user1 = new User("email1@gmail.com", "login1", "name1", LocalDate.now());
        userDbStorage.create(user1);
        User user2 = new User("email2@gmail.com", "login2", "name2", LocalDate.now());
        userDbStorage.create(user2);
        User user3 = new User("email3@gmail.com", "login3", "name3", LocalDate.now());
        userDbStorage.create(user3);

        Film film1 = new Film("name1", "description1", LocalDate.now(), 100);
        film1.setMpa(new MPA(3L, null));
        Film film2 = new Film("name2", "description2", LocalDate.now(), 110);
        film2.setMpa(new MPA(3L, null));
        Film film3 = new Film("name3", "description3", LocalDate.now(), 120);
        film3.setMpa(new MPA(3L, null));

        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        filmDbStorage.create(film3);
    }

    @Test
    public void testGetRecommendations() {
        likeDbStorage.addLike(1, 1);
        likeDbStorage.addLike(1, 2);

        likeDbStorage.addLike(2, 1);
        likeDbStorage.addLike(2, 2);
        likeDbStorage.addLike(2, 3);

        likeDbStorage.addLike(3, 1);

        List<Film> expectedValue = List.of(filmDbStorage.getById(3));
        List<Film> resultMethod = recomendationService.getRecommendations(1);

        assertThat(resultMethod).isEqualTo(expectedValue);
    }
}
