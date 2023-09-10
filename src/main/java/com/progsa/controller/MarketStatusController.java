package com.progsa.controller;

import com.progsa.service.MarketStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/market-status")
public class MarketStatusController {

    private final MarketStatusService marketStatusService;

    @Autowired
    public MarketStatusController(MarketStatusService marketStatusService) {
        this.marketStatusService = marketStatusService;
    }

    @GetMapping("/gainers")
    public ResponseEntity<String> getTopGainers() {
        try {
            ResponseEntity<String> response = marketStatusService.getTopGainers();
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching top gainers controller: " + e.getMessage());
        }
    }

    @GetMapping("/losers")
    public ResponseEntity<String> getTopLosers() {
        try {
            ResponseEntity<String> response = marketStatusService.getTopLosers();
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching top losers: " + e.getMessage());
        }
    }
}

//@RestController
//public class MarketStatusController {
//    private final MarketStatusService marketStatusService;
//
//    public MarketStatusController(MarketStatusService marketStatusService) {
//        this.marketStatusService = marketStatusService;
//    }
//
//    @GetMapping("/api/market-status")
//    public String getMarketStatus() {
//        return marketStatusService.getMarketStatus();
//    }
//}
