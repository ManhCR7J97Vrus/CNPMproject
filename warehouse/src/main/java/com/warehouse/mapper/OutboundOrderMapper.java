package com.warehouse.mapper;

import com.warehouse.dto.request.OutboundOrderRequest;
import com.warehouse.dto.response.OutboundOrderResponse;
import com.warehouse.entity.OutboundOrder;

/** Hợp đồng chuyển đơn xuất sang DTO công khai. */
public interface OutboundOrderMapper {
    OutboundOrderResponse toResponse(OutboundOrder entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    OutboundOrder toEntity(OutboundOrderRequest.Create request);
}
