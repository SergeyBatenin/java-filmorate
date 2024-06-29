package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

/**
 * User.
 */
@Data
@Builder
public class User {
    private Long id;
    @NonNull
    @Email
    @Size(max = 255)
    private String email;
    @NotBlank
    @Size(max = 100)
    private String login;
    @Size(max = 255)
    private String name;
    @PastOrPresent
    private LocalDate birthday;
}