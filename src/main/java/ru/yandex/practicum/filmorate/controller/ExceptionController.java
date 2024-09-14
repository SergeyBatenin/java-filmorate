package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ErrorMessage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.SaveDataException;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(NotFoundException exception) {
        log.error("ERROR", exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValid(Exception exception) {
        log.error("ERROR", exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(SaveDataException.class)
    public ResponseEntity<ErrorMessage> handleSaveData(SaveDataException exception) {
        log.error("ERROR", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handle(Exception exception) {
        log.error("ERROR", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(exception.getMessage()));
    }
}
