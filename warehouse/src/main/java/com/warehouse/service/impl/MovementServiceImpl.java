package com.warehouse.service.impl;

import com.warehouse.dto.request.MovementRequest;
import com.warehouse.dto.response.MovementResponse;
import com.warehouse.entity.Batch;
import com.warehouse.entity.Inventory;
import com.warehouse.entity.Location;
import com.warehouse.exception.AppException;
import com.warehouse.exception.ErrorCode;
import com.warehouse.mapper.MovementMapper;
import com.warehouse.repository.BatchRepository;
import com.warehouse.repository.InventoryMovementRepository;
import com.warehouse.repository.LocationRepository;
import com.warehouse.service.MovementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.warehouse.util.WarehouseRules.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {
    private final InventoryMovementRepository movements;
    private final BatchRepository batches;
    private final LocationRepository locations;
    private final StockLedger ledger;
    private final AccountActor actor;
    private final StockWriteLock lock;

    @Override
    @Transactional(readOnly = true)
    public List<MovementResponse> listMovements(String referenceNo) {
        String reference = optional(referenceNo);
        var entries = reference == null ? movements.findAllByOrderByCreatedAtAscIdAsc()
                : movements.findByReferenceNoIgnoreCaseOrderByCreatedAtAscIdAsc(reference);
        return entries.stream().map(MovementMapper::toResponse).toList();
    }

    @Override
    public MovementResponse transferStock(MovementRequest.Transfer request) {
        lock.acquire();
        Batch batch = batches.findById(request.batchId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Location from = location(request.fromLocationId());
        Location to = location(request.toLocationId());
        Inventory stock = ledger.existing(batch, from);
        String reference = documentNo("DC");
        ledger.move(stock, to, positive(request.quantity(), "quantity"), false, reference, actor.require(),
                optional(request.reason()));
        return MovementMapper.toResponse(movements.findByReferenceNoIgnoreCaseOrderByCreatedAtAscIdAsc(reference).get(0));
    }

    private Location location(Long id) {
        return locations.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy ô kho"));
    }
}
