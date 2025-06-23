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

public class UserTest {
    private Validator validator;

    @BeforeEach
    void beforeEach() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void correctUserTest() {
        User user = new User("margomargo123@gmail.com", "testlogin", null, LocalDate.now());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).hasSize(0);
    }

    @Test
    void incorrectEmailTest() {
        User user1 = new User("margomargo123gmail.com", "testlogin", null, LocalDate.now());
        User user2 = new User("margomargo123gmail.com@", "testlogin", null, LocalDate.now());
        User user3 = new User(null, "testlogin", null, LocalDate.now());
        List<ConstraintViolation<User>> violations = new ArrayList<>();
        violations.addAll(validator.validate(user1));
        violations.addAll(validator.validate(user2));
        violations.addAll(validator.validate(user3));
        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Электронная почта должна соответствовать своему формату",
                        "Электронная почта должна соответствовать своему формату",
                        "Электронная почта не может быть пустой");
    }

    @Test
    void incorrectLoginTest() {
        User user1 = new User("margomargo123@gmail.com", "test login", null, LocalDate.now());
        User user2 = new User("margomargo123@gmail.com", null, null, LocalDate.now());
        List<ConstraintViolation<User>> violations = new ArrayList<>();
        violations.addAll(validator.validate(user1));
        violations.addAll(validator.validate(user2));
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Логин не может быть пустым",
                        "Логин не может содержать пробелы");
    }

    @Test
    void incorrectBDTest() {
        User user1 = new User("margomargo123@gmail.com", "testlogin", null, LocalDate.of(2026, 4, 9));
        User user2 = new User("margomargo123@gmail.com", "testlogin", null, null);
        List<ConstraintViolation<User>> violations = new ArrayList<>();
        violations.addAll(validator.validate(user1));
        violations.addAll(validator.validate(user2));
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Дата рождения не может быть в будущем");
    }
}
