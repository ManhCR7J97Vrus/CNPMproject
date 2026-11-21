package com.warehouse.util;

import java.time.LocalDate;

/** Điểm mở rộng để sinh mã lô hàng. */
public final class BatchNoGenerator {
    private BatchNoGenerator() {}

    public static String generate(String skuCode, LocalDate mfgDate) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
