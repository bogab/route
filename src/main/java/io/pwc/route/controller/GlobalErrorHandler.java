package io.pwc.route.controller;

import io.pwc.route.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.Instant;


@Component
@ControllerAdvice
public class GlobalErrorHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler(value = {IllegalArgumentException.class})
    ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        log.error("Handling illegal argument error", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    ResponseEntity<ErrorResponse> handleValidationException(ConstraintViolationException exception) {
        log.error("Handling validation error", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleUnknownError(Exception exception) {
        log.error("Handling internal server error", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()));
    }

}
