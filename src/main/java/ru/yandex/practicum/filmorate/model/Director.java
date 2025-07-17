package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.Marker;

@Data
public class Director {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private int id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}
