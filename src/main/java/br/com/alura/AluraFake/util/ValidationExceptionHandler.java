package br.com.alura.AluraFake.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<ErrorItemDTO>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrorItemDTO> errors = ex.getBindingResult().getFieldErrors().stream().map(ErrorItemDTO::new).toList();
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<List<ErrorItemDTO>> handleResponseStatusException(ResponseStatusException ex) {
        ErrorItemDTO error = new ErrorItemDTO("global", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(List.of(error));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<List<ErrorItemDTO>> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorItemDTO error = new ErrorItemDTO("global", ex.getMessage());
        return ResponseEntity.badRequest().body(List.of(error));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<List<ErrorItemDTO>> handleIllegalState(IllegalStateException ex) {
        ErrorItemDTO error = new ErrorItemDTO("global", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(List.of(error));
    }
}