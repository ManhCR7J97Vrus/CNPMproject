package com.warehouse.repository;

import com.warehouse.entity.OutboundOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng outbound_orders. */
@Repository
public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long> {
    List<OutboundOrder> findByCustomerId(Long customerId);
    Optional<OutboundOrder> findByDispatchNoIgnoreCase(String dispatchNo);
    Optional<OutboundOrder> findByOrderNoIgnoreCase(String orderNo);
}
