package com.progsa.controller;

import com.progsa.service.StockListingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock-listing")
public class StockListingController {
    private final StockListingService stockListingService;

    public StockListingController(StockListingService stockListingService) {
        this.stockListingService = stockListingService;
    }

    @GetMapping("/ticker-search")
    public ResponseEntity<String> searchTicker(@RequestParam(value = "query", required=true) String keyword) {
        try{
            return stockListingService.tickerSearch(keyword);
        } catch (Exception e) {
            // Handle the exception and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while performing ticker.");
        }
    }

    @GetMapping("/stock-price-history")
    public ResponseEntity<String> getStockPriceHistory(@RequestParam(value = "query", required=true) String symbol) {
        //return stockListingService.getPriceHistory(stockName);
        try {
            return stockListingService.getPriceHistory(symbol);
        } catch (Exception e) {
            // Handle the exception and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching price history.");
        }
    }

    @GetMapping("/stock-price")
    public ResponseEntity<String> getStockPrice(@RequestParam(value = "query", required=true) String stockName) {
        try {
            return stockListingService.getStockPrice(stockName);
        } catch (Exception e) {
            // Handle the exception and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching latest price.");
        }
    }
}
