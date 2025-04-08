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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    private Validator validator;

    @BeforeEach
    void beforeEach() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void CorrectUserTest() {
        User user = new User("margomargo123@gmail.com", "testlogin", null, LocalDate.now());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
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
        assertEquals(3, violations.size());
        assertThat(violations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder
                ("Электронная почта должна соответствовать своему формату",
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
        assertEquals(2, violations.size());
        assertThat(violations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder
                ("Логин не может быть пустым",
                        "Логин не может содержать пробелы");
    }

    @Test
    void incorrectBDTest() {
        User user1 = new User("margomargo123@gmail.com", "testlogin", null, LocalDate.of(2025, 5, 9));
        User user2 = new User("margomargo123@gmail.com", "testlogin", null, null);
        List<ConstraintViolation<User>> violations = new ArrayList<>();
        violations.addAll(validator.validate(user1));
        violations.addAll(validator.validate(user2));
        assertEquals(1, violations.size());
        assertThat(violations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder
                ("Дата рождения не может быть в будущем");
    }
}
