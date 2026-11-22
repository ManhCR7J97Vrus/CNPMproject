package com.warehouse.mapper;

import com.warehouse.dto.response.StocktakeResponse;
import com.warehouse.entity.*;
import java.util.List;

public interface StocktakeMapper {
    static StocktakeResponse toResponse(Stocktake count, List<StocktakeLine> lines) {
        return new StocktakeResponse(count.getId(), count.getReferenceNo(), count.getStatus(), count.getReason(),
                count.getSnapshotAt(), count.getCreatedBy().getUsername(), count.getCounter().getId(),
                count.getCounter().getUsername(), count.getApprovedBy() == null ? null : count.getApprovedBy().getUsername(),
                count.getApprovedAt(), count.getLocations().stream().map(Location::getId).sorted().toList(),
                lines.stream().map(StocktakeMapper::line).toList());
    }

    private static StocktakeResponse.Line line(StocktakeLine line) {
        Inventory stock = line.getInventory();
        return new StocktakeResponse.Line(line.getId(), stock.getLocation().getId(), stock.getLocation().getCode(),
                stock.getBatch().getId(), stock.getBatch().getBatchNo(), stock.getBatch().getProduct().getSkuCode(),
                line.getExpectedQty(), line.getCountedQty(), line.getCountedQty() == null ? null
                    : line.getCountedQty().subtract(line.getExpectedQty()), line.getReason());
    }

    static StocktakeResponse.Quarantine quarantine(QuarantineRequest request) {
        Inventory stock = request.getInventory();
        Location destination = request.getDestination();
        return new StocktakeResponse.Quarantine(request.getId(), request.getReferenceNo(), request.getStatus(),
                stock.getBatch().getId(), stock.getBatch().getBatchNo(), stock.getLocation().getId(),
                stock.getLocation().getCode(), destination == null ? null : destination.getId(),
                destination == null ? null : destination.getCode(), request.getQuantity(), request.getReason(),
                request.getCreatedBy().getUsername(), request.getCreatedAt(),
                request.getApprovedBy() == null ? null : request.getApprovedBy().getUsername(),
                request.getApprovedAt(), request.getCompletedAt());
    }
}
