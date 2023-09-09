package com.progsa.dao;

import com.progsa.model.TransactionEntity;

import java.util.List;

public interface TransactionDao {
    public TransactionEntity createTransaction(TransactionEntity transaction);

    public double calculateNetStockProfitLoss(String email, String symbol);

    public List<TransactionEntity> getTransactionByEmail(String email);
}
