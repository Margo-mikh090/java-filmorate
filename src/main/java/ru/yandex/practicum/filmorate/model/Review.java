package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Data
@Builder
public class Review {
    private Long reviewId;

    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва (положительный/отрицательный) должен быть указан")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя должен быть указан")
    private Long userId;

    @NotNull(message = "ID фильма должен быть указан")
    private Long filmId;

    @PositiveOrZero(message = "Рейтинг полезности не может быть отрицательным")
    private Integer useful = 0;
}