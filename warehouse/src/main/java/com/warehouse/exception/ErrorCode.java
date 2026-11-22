package com.warehouse.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** Mã lỗi ổn định và trạng thái HTTP tương ứng. */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "Chưa cài đặt"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu"),
    DUPLICATE(HttpStatus.CONFLICT, "Mã đã được sử dụng"),
    INVALID_STATE(HttpStatus.CONFLICT, "Trạng thái hiện tại không cho phép thao tác"),
    ALREADY_CONFIRMED(HttpStatus.CONFLICT, "Phiếu đã được xác nhận"),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "Số lượng vượt tồn khả dụng"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Cần xác thực"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Không đủ quyền thực hiện");

    private final HttpStatus status;
    private final String message;
}
