package com.warehouse.service.impl;

import com.warehouse.dto.request.InboundOrderRequest;
import com.warehouse.dto.response.InboundOrderResponse;
import com.warehouse.service.InboundOrderService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Khung giao dịch cho phân hệ phiếu nhập. */
@Service
@Transactional
public class InboundOrderServiceImpl implements InboundOrderService {
    @Override
    @Transactional(readOnly = true)
    public List<InboundOrderResponse> listInboundOrders() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public InboundOrderResponse createInbound(InboundOrderRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    @Transactional(readOnly = true)
    public InboundOrderResponse getInbound(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public InboundOrderResponse updateInbound(Long id, InboundOrderRequest.Write request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public InboundOrderResponse receiveInbound(Long id, InboundOrderRequest.Receive request) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    @Override
    public InboundOrderResponse confirmInbound(Long id) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
