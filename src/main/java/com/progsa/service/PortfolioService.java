package com.progsa.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.progsa.IOModels.PortfolioOutputModel;
import com.progsa.IOModels.PortfolioStockModel;
import com.progsa.dao.PortfolioDao;
import com.progsa.dao.TransactionDao;
import com.progsa.model.PortfolioEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for all the User Portfolio based queries.
 */
@Service
@Slf4j
public class PortfolioService {

    private final PortfolioDao portfolioDao;
    private final TransactionDao transactionDao;
    private final StockListingService stockListingService;

    @Autowired
    public PortfolioService(PortfolioDao portfolioDao, StockListingService stockListingService, TransactionDao transactionDao) {
        this.portfolioDao = portfolioDao;
        this.stockListingService = stockListingService;
        this.transactionDao = transactionDao;
    }

    public ResponseEntity<PortfolioOutputModel> getPortfolioDetail(String email) {
        try {
            List<PortfolioEntity> portfolioEntityList = portfolioDao.findByEmail(email);
            List<PortfolioStockModel> portfolioStockModelList = new ArrayList<>();
            double netPortfolioValue = 0;
            double netPortfolioGain = 0;
            for (PortfolioEntity portfolioEntity : portfolioEntityList) {
                JsonNode curPriceNode = stockListingService.getCurrentPrice(portfolioEntity.getSymbol());
                double price = curPriceNode.get("c").asDouble();
                double percentagePriceChange = Math.round(curPriceNode.get("dp").asDouble()*1000.0)/1000.0;
                double cost = Math.round(price*portfolioEntity.getVolume()*100.0)/100.0;
                portfolioStockModelList.add(new PortfolioStockModel(portfolioEntity.getEmail(),
                        portfolioEntity.getStockName(), portfolioEntity.getSymbol(), price, portfolioEntity.getVolume(),
                        cost, percentagePriceChange, percentagePriceChange*portfolioEntity.getVolume()));
                netPortfolioValue+=cost;

                netPortfolioGain += cost - transactionDao.calculateNetStockProfitLoss(
                        portfolioEntity.getEmail(), portfolioEntity.getSymbol());
            }
            PortfolioOutputModel portfolioOutputModel = new PortfolioOutputModel(netPortfolioGain, netPortfolioValue, portfolioStockModelList);
            return ResponseEntity.ok(portfolioOutputModel);
        } catch(Exception e){
            log.error("Portfolio Service Error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
