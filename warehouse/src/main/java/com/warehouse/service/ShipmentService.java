package com.warehouse.service;

import com.warehouse.dto.request.ShipmentRequest;
import com.warehouse.dto.response.ShipmentResponse;
import java.security.Principal;
import java.util.List;

/** Hợp đồng nghiệp vụ cho phân hệ chuyến giao hàng. */
public interface ShipmentService {
    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    List<ShipmentResponse> myShipments(Principal actor);

    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    ShipmentResponse getShipment(Long id, Principal actor);

    /** Giới hạn dữ liệu theo danh tính của người gọi khi triển khai. */
    ShipmentResponse acceptShipment(Long id, ShipmentRequest.Receipt request, Principal actor);
}
