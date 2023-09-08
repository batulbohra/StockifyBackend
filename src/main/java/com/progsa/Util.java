package com.progsa;


import com.progsa.model.PortfolioEntity;
import com.progsa.model.TransactionEntity;

public class Util {
    public static TransactionEntity createTransactionEntity(String email, String stockName, String symbol,
                                                            double price, int volume, String transactionType) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setEmail(email);
        transaction.setStockName(stockName);
        transaction.setSymbol(symbol);
        transaction.setVolume(volume);
        transaction.setPrice(price);
        transaction.setCost(price * volume);
        transaction.setTransactionType(transactionType); // Set transaction type to "buy"
        return transaction;
    }

    public static PortfolioEntity createPortfolioEntity(String email, String stockName, String symbol, int volume) {
        PortfolioEntity portfolioEntry = new PortfolioEntity();
        portfolioEntry.setEmail(email);
        portfolioEntry.setStockName(stockName);
        portfolioEntry.setSymbol(symbol);
        portfolioEntry.setVolume(volume);
        return portfolioEntry;
    }
}

