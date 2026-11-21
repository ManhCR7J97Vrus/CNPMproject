package com.warehouse.util;

import org.springframework.data.domain.Pageable;

/** Điểm mở rộng để chuẩn hóa tham số phân trang. */
public final class PageUtil {
    private PageUtil() {}

    public static Pageable pageable(int page, int size) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
