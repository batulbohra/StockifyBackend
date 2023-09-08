package com.progsa.dao;

import com.progsa.model.TransactionEntity;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionDaoImpl implements TransactionDao {

    private final TransactionRepository transactionRepository;

//    private final EntityManager entityManager;

    @Autowired
    public TransactionDaoImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
//        this.entityManager = entityManager;
    }

    public TransactionEntity createTransaction(TransactionEntity transaction) {
        return transactionRepository.save(transaction);
    }

//    public double calculateNetVolumeOfStock(String email, String symbol) {
//        String queryString = "SELECT SUM(CASE WHEN t.transactionType = 'buy' THEN (t.volume) " +
//                "WHEN t.transactionType = 'sell' THEN -(t.volume) ELSE 0 END) " +
//                "FROM TransactionEntity t " +
//                "WHERE t.email = :email AND t.symbol = :symbol";
//
//        Query query = entityManager.createQuery(queryString);
//        query.setParameter("email", email);
//        query.setParameter("symbol", symbol);
//
//        Double result = (Double) query.getSingleResult();
//        return (result != null) ? result : 0.0;
//    }
}
