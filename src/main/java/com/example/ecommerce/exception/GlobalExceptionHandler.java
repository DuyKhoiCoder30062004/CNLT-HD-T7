package com.example.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("error", "Bad Request");
        response.put("message", "Dữ liệu đầu vào không hợp lệ");
        response.put("details", errors);

        return ResponseEntity.badRequest().body(response);
    }

    // 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", 404);
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 401
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", 401);
        response.put("error", "Unauthorized");
        response.put("message",  ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    //409
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateResource(
            DuplicateResourceException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", 409);
        response.put("error", "Conflict");
        response.put("message", ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(response);
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(
            Exception ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", 500);
        response.put("error", "Internal Server Error");
        response.put("message", "Đã xảy ra lỗi hệ thống");

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}
