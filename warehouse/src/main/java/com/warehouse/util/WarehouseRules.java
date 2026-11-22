package com.warehouse.util;

import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

public final class WarehouseRules {
    private WarehouseRules() {}

    public static String optional(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    public static String text(String value, String field) {
        String result = optional(value);
        require(result != null, field, "Cần nhập " + field);
        return result;
    }

    public static String code(String value, String field) {
        return text(value, field).toUpperCase(Locale.ROOT);
    }

    public static void require(boolean valid, String field, String message) {
        if (!valid) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, field, message);
        }
    }

    public static void state(boolean valid, String message) {
        if (!valid) {
            throw new AppException(ErrorCode.INVALID_STATE, message);
        }
    }

    public static String documentNo(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }

    public static BigDecimal amount(BigDecimal value, String field) {
        require(value != null && value.signum() >= 0, field, "Giá trị phải từ 0 trở lên");
        BigDecimal normalized = value.stripTrailingZeros();
        require(normalized.scale() <= 3 && normalized.precision() - normalized.scale() <= 15,
                field, "Chỉ chấp nhận tối đa 15 chữ số nguyên và 3 chữ số thập phân");
        return value;
    }

    public static BigDecimal positive(BigDecimal value, String field) {
        require(amount(value, field).signum() > 0, field, "Số lượng phải lớn hơn 0");
        return value;
    }
}
