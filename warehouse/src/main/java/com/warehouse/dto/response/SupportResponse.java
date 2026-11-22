package com.warehouse.dto.response;

import com.warehouse.entity.enums.SupportStatus;
import com.warehouse.entity.enums.SupportType;
import java.time.LocalDateTime;
import java.util.List;

public record SupportResponse(Long id, String referenceNo, Long outboundOrderId, String orderNo, String customer,
        SupportType type, SupportStatus status, String content, String response, LocalDateTime createdAt, List<Event> history) {

    public record Event(SupportStatus status, String actor, String response, LocalDateTime at) {}
}
