package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MPA {
    @NotNull
    private Long id;
    @NotBlank
    private String name;

    public MPA() {
    }
}
