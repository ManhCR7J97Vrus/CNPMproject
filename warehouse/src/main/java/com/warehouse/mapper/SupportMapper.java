package com.warehouse.mapper;

import com.warehouse.dto.response.SupportResponse;
import com.warehouse.entity.SupportEvent;
import com.warehouse.entity.SupportTicket;
import java.util.List;

public interface SupportMapper {
    static SupportResponse toResponse(SupportTicket ticket, List<SupportEvent> events, boolean customerView) {
        return new SupportResponse(ticket.getId(), ticket.getReferenceNo(), ticket.getOrder().getId(),
                ticket.getOrder().getOrderNo(), customerView ? null : ticket.getCustomer().getUsername(),
                ticket.getType(), ticket.getStatus(), ticket.getContent(), ticket.getResponse(), ticket.getCreatedAt(),
                events.stream().map(e -> new SupportResponse.Event(e.getStatus(), e.getActor().getFullName(),
                        e.getResponse(), e.getCreatedAt())).toList());
    }
}
