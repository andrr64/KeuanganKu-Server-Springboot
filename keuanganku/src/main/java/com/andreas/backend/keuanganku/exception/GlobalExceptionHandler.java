package com.andreas.backend.keuanganku.exception;

import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GeneralResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof UnrecognizedPropertyException) {
            return ResponseEntity.badRequest()
                    .body(new GeneralResponse<>("Field tidak dikenali", null, false));
        }

        return ResponseEntity.badRequest()
                .body(new GeneralResponse<>("Format JSON tidak valid", null, false));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GeneralResponse<?>> handleRuntime(RuntimeException ex) {
        System.out.println("Runtime exception occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GeneralResponse<>(ex.getMessage(), null, false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        System.out.println("Validation exception occurred: " + ex.getMessage());
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .findFirst()
                .orElse("Validasi gagal");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GeneralResponse<>(message, null, false));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponse<?>> handleOther(Exception ex) {
        System.out.println("Unhandled exception occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GeneralResponse<>("Terjadi kesalahan internal", null, false));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<GeneralResponse<?>> handleJwtException(JwtException ex) {
        System.out.println("JWT exception occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new GeneralResponse<>("Token tidak valid", null, false));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GeneralResponse<?>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        String message = String.format("Parameter '%s' harus bertipe %s yang valid", paramName, expectedType);

        return ResponseEntity.badRequest()
                .body(new GeneralResponse<>(message, null, false));
    }
}
