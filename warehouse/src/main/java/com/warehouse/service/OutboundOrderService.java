package com.warehouse.service;

import com.warehouse.dto.request.OutboundOrderRequest;
import com.warehouse.dto.response.OutboundOrderResponse;
import java.security.Principal;
import java.util.List;

public interface OutboundOrderService {
    List<OutboundOrderResponse> listOrders(OutboundOrderRequest.Search search, Principal actor);
    OutboundOrderResponse createOrder(OutboundOrderRequest.Create request, Principal actor);
    OutboundOrderResponse getOrder(Long id, Principal actor);
    OutboundOrderResponse.Plan suggest(Long id);
    OutboundOrderResponse approveOrder(Long id);
    OutboundOrderResponse reallocate(Long id, boolean returnedToStorage);
    OutboundOrderResponse pickOrder(Long id, OutboundOrderRequest.Pick request);
    OutboundOrderResponse packOrder(Long id, OutboundOrderRequest.Pack request);
    OutboundOrderResponse issueDispatch(Long id);
    OutboundOrderResponse handoverOrder(Long id, OutboundOrderRequest.Handover request);
    OutboundOrderResponse cancelOrder(Long id, OutboundOrderRequest.Cancel request);
    OutboundOrderResponse requestCancel(Long id, OutboundOrderRequest.CancelRequest request, Principal actor);
}
