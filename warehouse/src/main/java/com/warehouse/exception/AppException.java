package com.warehouse.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode code;
    private final Map<String, String> fields;

    public AppException(ErrorCode code) {
        this(code, code.getMessage());
    }

    public AppException(ErrorCode code, String message) {
        super(message);
        this.code = code;
        this.fields = Map.of();
    }

    public AppException(ErrorCode code, String field, String message) {
        super(message);
        this.code = code;
        this.fields = Map.of(field, message);
    }
}
