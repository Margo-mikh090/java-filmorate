package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class NotBeforeMovieBDValidator implements ConstraintValidator<NotBeforeMovieBD, LocalDate> {
    private static final LocalDate MOVIES_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) return true;
        return !localDate.isBefore(MOVIES_BIRTHDAY);
    }
}
