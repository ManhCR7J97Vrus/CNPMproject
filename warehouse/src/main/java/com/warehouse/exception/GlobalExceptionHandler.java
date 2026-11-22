package com.warehouse.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Chuẩn hóa lỗi API và chi tiết kiểm tra dữ liệu theo từng trường. */
@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ErrorBody(String code, String message, Map<String, String> fields) {}
    public record Envelope(ErrorBody error) {}

    public static Envelope envelope(ErrorCode code, Map<String, String> fields) {
        return new Envelope(new ErrorBody(code.name(), code.getMessage(), fields));
    }

    private ResponseEntity<Envelope> response(ErrorCode code, Map<String, String> fields) {
        return ResponseEntity.status(code.getStatus()).body(envelope(code, fields));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Envelope> notImplemented() {
        return response(ErrorCode.NOT_IMPLEMENTED, Map.of());
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Envelope> applicationError(AppException exception) {
        return ResponseEntity.status(exception.getCode().getStatus()).body(new Envelope(
                new ErrorBody(exception.getCode().name(), exception.getMessage(), exception.getFields())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Envelope> invalidFields(MethodArgumentNotValidException exception) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return response(ErrorCode.VALIDATION_ERROR, fields);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Envelope> integrityConflict() {
        return response(ErrorCode.INVALID_STATE, Map.of());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            HandlerMethodValidationException.class})
    public ResponseEntity<Envelope> invalidInput() {
        return response(ErrorCode.VALIDATION_ERROR, Map.of());
    }
}
