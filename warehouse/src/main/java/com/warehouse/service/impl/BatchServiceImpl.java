package com.warehouse.service.impl;

import com.warehouse.dto.request.BatchRequest;
import com.warehouse.dto.response.BatchResponse;
import com.warehouse.dto.response.MovementResponse;
import com.warehouse.entity.Batch;
import com.warehouse.entity.Inventory;
import com.warehouse.entity.enums.BatchStatus;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.BatchMapper;
import com.warehouse.mapper.MovementMapper;
import com.warehouse.repository.BatchRepository;
import com.warehouse.repository.InventoryMovementRepository;
import com.warehouse.repository.InventoryRepository;
import com.warehouse.service.BatchService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {
    private final BatchRepository batches;
    private final InventoryRepository inventory;
    private final InventoryMovementRepository movements;
    private final StockLedger ledger;

    @Override
    public List<BatchResponse> listBatches(BatchRequest.Search search) {
        LocalDate today = ledger.today();
        return batches.findAll().stream().filter(batch -> search.productId() == null
                        || batch.getProduct().getId().equals(search.productId()))
                .map(batch -> response(batch, today)).filter(batch -> search.status() == null || batch.status() == search.status())
                .sorted(Comparator.comparing(BatchResponse::skuCode).thenComparing(BatchResponse::batchNo)).toList();
    }

    @Override
    public BatchResponse getBatch(Long id) {
        return response(find(id), ledger.today());
    }

    @Override
    public List<MovementResponse> traceBatch(Long id) {
        find(id);
        return movements.findByBatchIdOrderByCreatedAtAscIdAsc(id).stream().map(MovementMapper::toResponse).toList();
    }

    @Override
    public BatchResponse.Alerts alerts() {
        LocalDate today = ledger.today();
        List<BatchResponse> expired = new ArrayList<>();
        List<BatchResponse> expiring = new ArrayList<>();
        var inStock = inventory.findByQtyOnHandGreaterThan(BigDecimal.ZERO).stream().map(Inventory::getBatch)
                .collect(Collectors.toMap(Batch::getId, batch -> batch, (one, two) -> one));
        inStock.values().stream().filter(batch -> batch.getProduct().isHasExpiry() && batch.getExpiryDate() != null)
                .sorted(Comparator.comparing(Batch::getExpiryDate).thenComparing(Batch::getBatchNo))
                .forEach(batch -> {
                    if (batch.getExpiryDate().isBefore(today)) {
                        expired.add(response(batch, today));
                    } else if (!batch.getExpiryDate().isAfter(today.plusDays(batch.getProduct().getExpiryAlertDays()))) {
                        expiring.add(response(batch, today));
                    }
                });
        return new BatchResponse.Alerts(today, List.copyOf(expired), List.copyOf(expiring));
    }

    private static BatchResponse response(Batch batch, LocalDate today) {
        boolean expired = batch.getProduct().isHasExpiry() && batch.getExpiryDate() != null
                && batch.getExpiryDate().isBefore(today);
        return BatchMapper.toResponse(batch, expired ? BatchStatus.EXPIRED : batch.getStatus());
    }

    private Batch find(Long id) {
        return batches.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy lô"));
    }
}
