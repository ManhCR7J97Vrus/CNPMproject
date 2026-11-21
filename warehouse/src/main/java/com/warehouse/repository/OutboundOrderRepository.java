package com.warehouse.repository;

import com.warehouse.entity.OutboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng outbound_orders. */
@Repository
public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long> {
}
