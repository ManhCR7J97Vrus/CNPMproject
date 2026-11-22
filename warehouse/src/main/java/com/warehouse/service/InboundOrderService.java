package com.warehouse.service;

import com.warehouse.dto.request.InboundOrderRequest;
import com.warehouse.dto.response.InboundOrderResponse;
import java.util.List;

public interface InboundOrderService {
    List<InboundOrderResponse> listInboundOrders(String reference);
    InboundOrderResponse createInbound(InboundOrderRequest.Write request);
    InboundOrderResponse getInbound(Long id);
    InboundOrderResponse updateInbound(Long id, InboundOrderRequest.Write request);
    InboundOrderResponse receiveInbound(Long id, InboundOrderRequest.Receive request);
    InboundOrderResponse confirmInbound(Long id, InboundOrderRequest.Placements placements);
    InboundOrderResponse postInbound(Long id, InboundOrderRequest.Placements actual);
    InboundOrderResponse createReturn(InboundOrderRequest.Return request);
}
