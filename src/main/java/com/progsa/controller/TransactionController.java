package com.progsa.controller;

import com.progsa.IOModels.TransactionHistoryModel;
import com.progsa.IOModels.TransactionInputModel;
import com.progsa.IOModels.TransactionOutputModel;
import com.progsa.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.progsa.Constants.ERROR_MESSAGE;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(value="/buy", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionOutputModel> buyStock(@RequestBody TransactionInputModel transactionInput) {
        try {
            ResponseEntity<TransactionOutputModel> response = transactionService.buyStock(transactionInput);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Handle internal server error
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE, e);
        }
    }

    @PostMapping(value="/sell", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionOutputModel> sellStock(@RequestBody TransactionInputModel transactionInput) {
        try {
            ResponseEntity<TransactionOutputModel> response = transactionService.sellStock(transactionInput);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Handle internal server error
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE, e);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionHistoryModel>> getTransactionHistoryForUser(
            @RequestParam(value = "query", required=true) String email){
        try {
            ResponseEntity<List<TransactionHistoryModel>> response = transactionService.getTransactionHistory(email);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Handle internal server error
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE, e);
        }
    }
}
