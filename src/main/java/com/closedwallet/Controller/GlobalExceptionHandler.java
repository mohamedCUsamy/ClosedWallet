package com.closedwallet.Controller;

import com.closedwallet.Exception.InvalidPassOrEmail;
import com.closedwallet.Exception.UserExisitsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Email/phone already registered
    @ExceptionHandler(UserExisitsException.class)
    public ResponseEntity<Map<String, String>> handleUserExists(UserExisitsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorBody(ex.getMessage()));
    }

    // Bad email/password on login
    @ExceptionHandler(InvalidPassOrEmail.class)
    public ResponseEntity<Map<String, String>> handleInvalidLogin(InvalidPassOrEmail ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorBody(ex.getMessage()));
    }

    // Catch-all for plain Exception (e.g. "Passwords do not match" thrown in UserService)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorBody(ex.getMessage()));
    }

    private Map<String, String> errorBody(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("responseCode", "400");
        body.put("responseMessage", "Error");
        body.put("responseDescription", message);
        return body;
    }
}