package com.warehouse.repository;

import com.warehouse.entity.InboundItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng inbound_items. */
@Repository
public interface InboundItemRepository extends JpaRepository<InboundItem, Long> {
    List<InboundItem> findByInboundOrderIdOrderById(Long orderId);
    List<InboundItem> findByReturnItemId(Long itemId);
    void deleteByInboundOrderId(Long orderId);
}
