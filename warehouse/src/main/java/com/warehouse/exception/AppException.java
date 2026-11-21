package com.warehouse.exception;

import lombok.Getter;

/** Mang mã lỗi nghiệp vụ từ service đến bộ xử lý lỗi HTTP. */
@Getter
public class AppException extends RuntimeException {
    private final ErrorCode code;

    public AppException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
