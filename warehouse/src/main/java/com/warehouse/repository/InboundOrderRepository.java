package com.warehouse.repository;

import com.warehouse.entity.InboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng inbound_orders. */
@Repository
public interface InboundOrderRepository extends JpaRepository<InboundOrder, Long> {
}
