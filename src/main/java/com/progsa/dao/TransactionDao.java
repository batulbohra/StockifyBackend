package com.progsa.dao;

import com.progsa.model.TransactionEntity;

public interface TransactionDao {
    public TransactionEntity createTransaction(TransactionEntity transaction);
}
