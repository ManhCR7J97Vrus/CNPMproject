package com.warehouse.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record StorageProfileResponse(String code, BigDecimal temperatureMin, BigDecimal temperatureMax,
        BigDecimal humidityMin, BigDecimal humidityMax, boolean avoidLight, boolean fireProtection,
        List<String> incompatibleGroups) {}
