package com.warehouse.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class BatchNoGenerator {
    private BatchNoGenerator() {}

    public static String generate(String skuCode, LocalDate mfgDate) {
        String prefix = mfgDate == null ? skuCode : skuCode + "-" + mfgDate.format(DateTimeFormatter.BASIC_ISO_DATE);
        return WarehouseRules.documentNo(prefix);
    }
}
