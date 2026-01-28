package com.antmillion.stock.controller;

import com.antmillion.stock.dto.StockDTO;
import com.antmillion.stock.service.KisStockSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sync-test")
@RequiredArgsConstructor
public class StockSyncController {

    private final KisStockSyncService kisStockSyncService;

    @GetMapping("/kospi")
    public List<StockDTO> getKospi() throws Exception {
        return kisStockSyncService.loadKospiStocks(System.getProperty("user.dir"));
    }

    @GetMapping("/kosdaq")
    public List<StockDTO> getKosdaq() throws Exception {
        return kisStockSyncService.loadKosdaqStocks(System.getProperty("user.dir"));
    }

}
