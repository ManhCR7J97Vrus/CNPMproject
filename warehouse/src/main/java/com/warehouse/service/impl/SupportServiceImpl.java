package com.warehouse.service.impl;

import com.warehouse.dto.request.SupportRequest;
import com.warehouse.dto.response.SupportResponse;
import com.warehouse.entity.*;
import com.warehouse.entity.enums.Role;
import com.warehouse.entity.enums.SupportStatus;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.SupportMapper;
import com.warehouse.repository.OutboundOrderRepository;
import com.warehouse.repository.SupportEventRepository;
import com.warehouse.repository.SupportTicketRepository;
import com.warehouse.service.SupportService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {
    private final SupportTicketRepository tickets;
    private final SupportEventRepository events;
    private final OutboundOrderRepository orders;
    private final AccountActor account;
    private final StockLedger ledger;
    private final StockWriteLock lock;

    @Override
    @Transactional(readOnly = true)
    public List<SupportResponse> list(Principal principal) {
        User user = account.require(principal);
        boolean customer = user.getRole() == Role.CUSTOMER;
        var list = customer ? tickets.findByCustomerIdOrderByCreatedAtDescIdDesc(user.getId())
                : tickets.findAll(Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        return list.stream().map(ticket -> SupportMapper.toResponse(ticket, List.of(), customer)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupportResponse get(Long id, Principal principal) {
        User user = account.require(principal);
        boolean customer = user.getRole() == Role.CUSTOMER;
        SupportTicket ticket = find(id);
        if (customer && !ticket.getCustomer().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy yêu cầu hỗ trợ");
        }
        return response(ticket, customer);
    }

    @Override
    public SupportResponse create(SupportRequest.Create request, Principal principal) {
        lock.acquire();
        User user = account.require(principal);
        OutboundOrder order = orders.findById(request.outboundOrderId())
                .filter(row -> row.getCustomer().getId().equals(user.getId()))
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy đơn"));
        SupportTicket ticket = new SupportTicket();
        ticket.setReferenceNo(documentNo("KN"));
        ticket.setOrder(order);
        ticket.setCustomer(user);
        ticket.setType(request.type());
        ticket.setStatus(SupportStatus.NEW);
        ticket.setContent(text(request.content(), "content"));
        ticket.setCreatedAt(ledger.now());
        tickets.save(ticket);
        event(ticket, user);
        return response(ticket, true);
    }

    @Override
    public SupportResponse review(Long id, SupportRequest.Review request, Principal principal) {
        lock.acquire();
        User manager = account.require(principal);
        SupportTicket ticket = find(id);
        boolean taking = ticket.getStatus() == SupportStatus.NEW && request.status() == SupportStatus.IN_PROGRESS;
        boolean resolving = ticket.getStatus() == SupportStatus.IN_PROGRESS && request.status() == SupportStatus.RESOLVED;
        state(taking || resolving, "Yêu cầu cần được tiếp nhận trước khi kết thúc");
        ticket.setResponse(resolving ? text(request.response(), "response") : optional(request.response()));
        ticket.setStatus(request.status());
        event(ticket, manager);
        return response(ticket, false);
    }

    private void event(SupportTicket ticket, User actor) {
        SupportEvent event = new SupportEvent();
        event.setTicket(ticket);
        event.setActor(actor);
        event.setStatus(ticket.getStatus());
        event.setResponse(ticket.getResponse());
        event.setCreatedAt(ledger.now());
        events.save(event);
    }

    private SupportTicket find(Long id) {
        return tickets.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy yêu cầu hỗ trợ"));
    }

    private SupportResponse response(SupportTicket ticket, boolean customer) {
        return SupportMapper.toResponse(ticket, events.findByTicketIdOrderByCreatedAtAscIdAsc(ticket.getId()), customer);
    }
}
