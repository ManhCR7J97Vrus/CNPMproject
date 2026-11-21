package com.warehouse.service;

import com.warehouse.dto.request.InboundOrderRequest;
import com.warehouse.dto.response.InboundOrderResponse;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ phiếu nhập. */
public interface InboundOrderService {
    List<InboundOrderResponse> listInboundOrders();

    InboundOrderResponse createInbound(InboundOrderRequest.Write request);

    InboundOrderResponse getInbound(Long id);

    InboundOrderResponse updateInbound(Long id, InboundOrderRequest.Write request);

    InboundOrderResponse receiveInbound(Long id, InboundOrderRequest.Receive request);

    /** Tạo lô, cộng tồn, ghi sổ và xác nhận trong cùng transaction. */
    InboundOrderResponse confirmInbound(Long id);
}
