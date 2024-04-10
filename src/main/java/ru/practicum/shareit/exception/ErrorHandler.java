package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage duplicateEmailExceptionHandler(DuplicateEmailException e) {
        log.info("Ошибка, дублированный email");
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage notFoundExceptionHandler(NotFoundException e) {
        log.info("Ошибка, объект не найден");
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage validationExceptionHandler(ValidationException e) {
        log.info("Некорректный запрос");
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(UnsupportedStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage unsupportedStatusExceptionHandler(UnsupportedStatusException e) {
        log.info("Некорректный запрос");
        return new ErrorMessage(e.getMessage());
    }
}
