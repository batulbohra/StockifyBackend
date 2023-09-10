package com.progsa.controller;

import com.progsa.IOModels.PortfolioOutputModel;
import com.progsa.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for portfolio related query.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/detail")
    public ResponseEntity<PortfolioOutputModel> getPortfolio(
            @RequestParam(value = "query", required=true) String email) {
        try {
            return portfolioService.getPortfolioDetail(email);
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
