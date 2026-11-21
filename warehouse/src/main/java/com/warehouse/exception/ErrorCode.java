package com.warehouse.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** Mã lỗi ổn định và trạng thái HTTP tương ứng. */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "Chưa cài đặt"),
    ALREADY_CONFIRMED(HttpStatus.CONFLICT, "Phiếu đã được xác nhận"),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "Số lượng vượt tồn khả dụng"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Cần xác thực"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Không đủ quyền thực hiện");

    private final HttpStatus status;
    private final String message;
}
