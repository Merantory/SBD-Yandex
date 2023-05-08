package com.merantory.YandexSBD.controllers;

import com.merantory.YandexSBD.util.exceptions.courier.CourierInvalidRequestParamsException;
import com.merantory.YandexSBD.util.exceptions.courier.CourierMetaInfoInvalidDateException;
import com.merantory.YandexSBD.util.exceptions.courier.CourierNotFoundException;
import com.merantory.YandexSBD.util.exceptions.order.OrderInvalidRequestParamsException;
import com.merantory.YandexSBD.util.exceptions.order.OrderNotCreatedException;
import com.merantory.YandexSBD.util.exceptions.order.OrderNotFoundException;
import com.merantory.YandexSBD.util.exceptions.order.OrderNotMarkCompleteException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // If not valid method's arguments was passed
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        // Get all errors
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);
    }

    // Catch exception on invalid data
    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<String> handle(ConstraintViolationException constraintViolationException) {
        Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
        String errorMessage = "";
        // If information about error validation not empty
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            violations.forEach(violation ->
                    builder.append(violation.getPropertyPath())
                            .append(" - ")
                            .append(violation.getMessage())
                            .append(";"));

            errorMessage = builder.toString();
        } else {
            errorMessage = "ConstraintViolationException occurred.";
        }
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    private ResponseEntity<HttpStatus> exceptionHandler(OrderNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(OrderInvalidRequestParamsException.class)
    private ResponseEntity<HttpStatus> exceptionHandler(OrderInvalidRequestParamsException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(OrderNotCreatedException.class)
    private ResponseEntity<HttpStatus> exceptionHandler(OrderNotCreatedException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(OrderNotMarkCompleteException.class)
    private ResponseEntity<HttpStatus> exceptionHandler(OrderNotMarkCompleteException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(CourierNotFoundException.class)
    private ResponseEntity<HttpStatus> exceptionHandler(CourierNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(CourierInvalidRequestParamsException.class)
    private ResponseEntity<HttpStatus> exceptionHandler(CourierInvalidRequestParamsException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(CourierMetaInfoInvalidDateException.class)
    private ResponseEntity<HttpStatus> exceptionHandler(CourierMetaInfoInvalidDateException exception) {
        return ResponseEntity.badRequest().build();
    }
}
