package com.assignment.accountservice.advice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class AccountResponseAdvice {

    private static final Logger logger =
            LoggerFactory.getLogger(AccountResponseAdvice.class);

    /**
     * Validation Exception Handler
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        logger.error("Validation failed for request {} {}",
                request.getMethod(),
                request.getRequestURI());

        Map<String, String> validationErrors = new LinkedHashMap<>();

        ex.getBindingResult()
          .getFieldErrors()
          .forEach(error ->
                  validationErrors.put(
                          error.getField(),
                          error.getDefaultMessage()));

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Failed");
        errorResponse.put("message", "Request validation failed");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("validationErrors", validationErrors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Runtime Exception Handler
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        logger.error("Runtime exception for request {} {}",
                request.getMethod(),
                request.getRequestURI(),
                ex);

        return buildErrorResponse(
                ex,
                request,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Generic Exception Handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Unhandled exception for request {} {}",
                request.getMethod(),
                request.getRequestURI(),
                ex);

        return buildErrorResponse(
                ex,
                request,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Common Error Response Builder
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            Exception ex,
            HttpServletRequest request,
            HttpStatus status) {

        Map<String, Object> errorResponse = new LinkedHashMap<>();

        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("exception", ex.getClass().getSimpleName());
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", request.getRequestURI());

        return ResponseEntity.status(status).body(errorResponse);
    }
}