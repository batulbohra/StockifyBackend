package com.progsa.controller;

import com.progsa.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/buy")
    public ResponseEntity<String> buyStock(
            @RequestParam String email,
            @RequestParam String stockName,
            @RequestParam String symbol,
            @RequestParam double price,
            @RequestParam int volume) {
        try {
            ResponseEntity<String> response = transactionService.buyStock(email, stockName, symbol, price, volume);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Handle internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while buying the stock");
        }
    }

    // Define other endpoints and methods for transaction-related operations here
}
