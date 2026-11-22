package com.warehouse.repository;

import com.warehouse.entity.InboundOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Cổng truy cập bảng inbound_orders. */
@Repository
public interface InboundOrderRepository extends JpaRepository<InboundOrder, Long> {
    List<InboundOrder> findBySupplierIdAndSupplierNoticeTrueOrderByCreatedAtDescIdDesc(Long supplierId);
    Optional<InboundOrder> findBySupplierIdAndExternalReferenceIgnoreCase(Long supplierId, String reference);
    Optional<InboundOrder> findByOrderNoIgnoreCase(String orderNo);
    boolean existsByReturnOfId(Long outboundOrderId);
}
