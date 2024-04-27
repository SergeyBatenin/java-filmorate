package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.FilmReleaseDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReleaseDateValidator implements ConstraintValidator<FilmReleaseDate, LocalDate> {
    private String date;
    @Override
    public void initialize(FilmReleaseDate constraintAnnotation) {
        date = constraintAnnotation.date();
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        if (releaseDate == null) {
            return false;
        }
        LocalDate validDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return releaseDate.isAfter(validDate);
    }
}
