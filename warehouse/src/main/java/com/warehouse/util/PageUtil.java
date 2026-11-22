package com.warehouse.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class PageUtil {
    private PageUtil() {}

    public static Pageable pageable(int page, int size) {
        WarehouseRules.require(page >= 0, "page", "Trang phải từ 0 trở lên");
        WarehouseRules.require(size >= 1 && size <= 100, "size", "Kích thước trang phải từ 1 đến 100");
        return PageRequest.of(page, size);
    }
}
