package com.progsa.dao;

import com.progsa.model.TransactionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class TransactionDaoImpl implements TransactionDao {

    private final TransactionRepository transactionRepository;

    private final EntityManager entityManager;

    @Autowired
    public TransactionDaoImpl(TransactionRepository transactionRepository, EntityManager entityManager) {
        this.transactionRepository = transactionRepository;
        this.entityManager = entityManager;
    }

    public TransactionEntity createTransaction(TransactionEntity transaction) {
        return transactionRepository.save(transaction);
    }

    public double calculateNetStockProfitLoss(String email, String symbol) {
        String queryString = "SELECT SUM(CASE WHEN t.transactionType = 'buy' THEN (t.cost) " +
                "WHEN t.transactionType = 'sell' THEN -(t.cost) ELSE 0 END) " +
                "FROM TransactionEntity t " +
                "WHERE t.email = :email AND t.symbol = :symbol";

        Query query = entityManager.createQuery(queryString);
        query.setParameter("email", email);
        query.setParameter("symbol", symbol);

        Double result = (Double) query.getSingleResult();
        return (result != null) ? result : 0.0;
    }

    public List<TransactionEntity> getTransactionByEmail(String email){
        log.info("Called");
        List<TransactionEntity> transactionEntities = transactionRepository.findByEmail(email);
        log.info(String.valueOf(transactionEntities.size()));
        return transactionEntities;
    }
}
