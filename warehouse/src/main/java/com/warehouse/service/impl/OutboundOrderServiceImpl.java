package com.warehouse.service.impl;

import com.warehouse.dto.request.OutboundOrderRequest;
import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.service.OutboundOrderService;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ đơn xuất. */
@Service
@Transactional
public class OutboundOrderServiceImpl implements OutboundOrderService {
    @Override
    @Transactional(readOnly = true)
    public List<OutboundOrderResponse> listOrders(Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse createOrder(OutboundOrderRequest.Create request, Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public OutboundOrderResponse getOrder(Long id, Principal actor) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse approveOrder(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse pickOrder(Long id, OutboundOrderRequest.Pick request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse packOrder(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse handoverOrder(Long id, OutboundOrderRequest.Handover request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public OutboundOrderResponse cancelOrder(Long id, OutboundOrderRequest.Cancel request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
