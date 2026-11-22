package com.warehouse.service;

import com.warehouse.dto.request.StocktakeRequest;
import com.warehouse.dto.response.StocktakeResponse;
import java.util.List;

public interface StocktakeService {
    List<StocktakeResponse> listStocktakes();
    List<StocktakeResponse.Counter> counters();
    StocktakeResponse getStocktake(Long id);
    StocktakeResponse createStocktake(StocktakeRequest.Create request);
    StocktakeResponse recordCount(Long id, StocktakeRequest.Count request);
    StocktakeResponse approveAdjustment(Long id, StocktakeRequest.Review request);
    StocktakeResponse returnForRecount(Long id, StocktakeRequest.Review request);
    StocktakeResponse refreshSnapshot(Long id);
    List<StocktakeResponse.Quarantine> quarantineRequests();
    StocktakeResponse.Quarantine requestQuarantine(StocktakeRequest.Quarantine request);
    StocktakeResponse.Quarantine approveQuarantine(Long id, StocktakeRequest.QuarantineApproval request);
    StocktakeResponse.Quarantine postQuarantine(Long id);
}
