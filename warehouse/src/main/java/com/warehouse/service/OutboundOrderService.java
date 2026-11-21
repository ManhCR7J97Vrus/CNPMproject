package com.warehouse.service;

import com.warehouse.dto.request.OutboundOrderRequest;
import com.warehouse.dto.response.OutboundOrderResponse;
import java.security.Principal;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ đơn xuất. */
public interface OutboundOrderService {
    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    List<OutboundOrderResponse> listOrders(Principal actor);

    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    OutboundOrderResponse createOrder(OutboundOrderRequest.Create request, Principal actor);

    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    OutboundOrderResponse getOrder(Long id, Principal actor);

    /** Khóa dòng tồn trước khi giữ chỗ trong cùng transaction. */
    OutboundOrderResponse approveOrder(Long id);

    OutboundOrderResponse pickOrder(Long id, OutboundOrderRequest.Pick request);

    OutboundOrderResponse packOrder(Long id);

    /** Trừ tồn, giải phóng giữ chỗ và ghi nhận bàn giao trong cùng transaction. */
    OutboundOrderResponse handoverOrder(Long id, OutboundOrderRequest.Handover request);

    OutboundOrderResponse cancelOrder(Long id, OutboundOrderRequest.Cancel request);
}
