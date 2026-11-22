package com.warehouse.service.impl;

import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.entity.OrderEvent;
import com.warehouse.entity.OutboundOrder;
import com.warehouse.entity.User;
import com.warehouse.repository.OrderEventRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class OrderHistory {
    private final OrderEventRepository events;
    private final StockLedger ledger;

    void add(OutboundOrder order, User actor, String type, String note) {
        add(order, actor, type, note, null, null);
    }

    void add(OutboundOrder order, User actor, String type, String note, LocalDateTime previous, LocalDateTime next) {
        OrderEvent event = new OrderEvent();
        event.setOrder(order);
        event.setActor(actor);
        event.setType(type);
        event.setNote(note);
        event.setCreatedAt(ledger.now());
        event.setPreviousAppointment(previous);
        event.setAppointment(next);
        events.save(event);
    }

    List<OutboundOrderResponse.Event> read(Long orderId, boolean customer) {
        return events.findByOrderIdOrderByCreatedAtAscIdAsc(orderId).stream()
                .map(e -> new OutboundOrderResponse.Event(e.getType(), e.getCreatedAt(), e.getNote(),
                        customer ? null : e.getActor().getUsername(), e.getPreviousAppointment(), e.getAppointment()))
                .toList();
    }
}
