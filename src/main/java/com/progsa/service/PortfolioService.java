package com.progsa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.progsa.IOModels.PortfolioOutputModel;
import com.progsa.dao.PortfolioDao;
import com.progsa.model.PortfolioEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PortfolioService {

    private final PortfolioDao portfolioDao;

    private final StockListingService stockListingService;

    @Autowired
    public PortfolioService(PortfolioDao portfolioDao, StockListingService stockListingService) {
        this.portfolioDao = portfolioDao;
        this.stockListingService = stockListingService;
    }

    public ResponseEntity<List<PortfolioOutputModel>> getPortfolioDetail(String email) {
        try {
            List<PortfolioEntity> portfolioEntityList = portfolioDao.findByEmail(email);
            List<PortfolioOutputModel> portfolioOutputModelList = new ArrayList<>();

            for (PortfolioEntity portfolioEntity : portfolioEntityList) {
                JsonNode curPriceNode = stockListingService.getCurrentPrice(portfolioEntity.getSymbol());
                double price = curPriceNode.get("c").asDouble();
                double percentagePriceChange = curPriceNode.get("dp").asDouble();
                portfolioOutputModelList.add(new PortfolioOutputModel(portfolioEntity.getEmail(),
                        portfolioEntity.getStockName(), portfolioEntity.getSymbol(), price, portfolioEntity.getVolume(),
                        price * portfolioEntity.getVolume(), percentagePriceChange));
            }

            return ResponseEntity.ok(portfolioOutputModelList);
        } catch(Exception e){
            log.error("Portfolio Service Error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
