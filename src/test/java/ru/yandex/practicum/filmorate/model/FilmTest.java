package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class FilmTest {
    private Validator validator;

    @BeforeEach
    void beforeEach() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void correctFilmTest() {
        Film film = new Film("name", "description", LocalDate.now(), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations).hasSize(0);
    }

    @Test
    void blankNameofFilmTest() {
        Film film1 = new Film("", "description", LocalDate.now(), 120);
        Film film2 = new Film("   ", "description", LocalDate.now(), 120);
        Film film3 = new Film(null, "description", LocalDate.now(), 120);

        List<ConstraintViolation<Film>> violations = new ArrayList<>();
        violations.addAll(validator.validate(film1));
        violations.addAll(validator.validate(film2));
        violations.addAll(validator.validate(film3));
        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Название не может быть пустым",
                        "Название не может быть пустым",
                        "Название не может быть пустым");
    }

    @Test
    void descriptionSizeTest() {
        Film film1 = new Film("name", "w".repeat(200), LocalDate.now(), 120);
        Film film2 = new Film("name", "w".repeat(201), LocalDate.now(), 120);
        List<ConstraintViolation<Film>> violations = new ArrayList<>();
        violations.addAll(validator.validate(film1));
        violations.addAll(validator.validate(film2));
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Описание не должно превышать 200 символов");
    }

    @Test
    void releaseBeforeMovieBDTest() {
        Film film1 = new Film("name", "description", LocalDate.of(1895, 12, 28), 120);
        Film film2 = new Film("name", "description", LocalDate.of(1894, 12, 28), 120);
        Film film3 = new Film("name", "description", null, 120);
        List<ConstraintViolation<Film>> violations = new ArrayList<>();
        violations.addAll(validator.validate(film1));
        violations.addAll(validator.validate(film2));
        violations.addAll(validator.validate(film3));
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Дата релиза не может быть раньше 28 декабря 1895 года");
    }

    @Test
    void durationNotPositiveTest() {
        Film film1 = new Film("name", "description", LocalDate.now(), 0);
        Film film2 = new Film("name", "description", LocalDate.now(), -1);
        List<ConstraintViolation<Film>> violations = new ArrayList<>();
        violations.addAll(validator.validate(film1));
        violations.addAll(validator.validate(film2));
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Продолжительность фильма должна быть положительным числом",
                        "Продолжительность фильма должна быть положительным числом");
    }
}
