package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private Long reviewId;
    @NotNull
    private Long filmId;
    @NotNull
    private Long userId;
    @NotBlank
    @Size(max = 200)
    private String content;
    private int useful;
    @NotNull
    private Boolean isPositive;
}
