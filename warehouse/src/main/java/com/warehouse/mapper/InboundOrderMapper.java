package com.warehouse.mapper;

import com.warehouse.dto.request.InboundOrderRequest;
import com.warehouse.dto.response.InboundOrderResponse;
import com.warehouse.entity.InboundOrder;

/** Hợp đồng chuyển phiếu nhập sang DTO công khai. */
public interface InboundOrderMapper {
    InboundOrderResponse toResponse(InboundOrder entity);

    /** Chỉ ánh xạ dữ liệu nhập; service chịu trách nhiệm xử lý các liên kết và quy tắc nghiệp vụ. */
    InboundOrder toEntity(InboundOrderRequest.Write request);
}
