package com.progsa.service;

import com.progsa.IOModels.TransactionHistoryModel;
import com.progsa.IOModels.TransactionInputModel;
import com.progsa.IOModels.TransactionOutputModel;
import com.progsa.dao.PortfolioDao;
import com.progsa.dao.TransactionDao;
import com.progsa.dao.UserInfoDao;
import com.progsa.model.PortfolioEntity;
import com.progsa.model.TransactionEntity;
import com.progsa.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for user transaction related activities.
 */
@Service
@Slf4j
public class TransactionService {

    private final UserInfoDao userInfoDao;
    private final TransactionDao transactionDao;

    private final PortfolioDao portfolioDao;

    @Autowired
    public TransactionService(UserInfoDao userInfoDao, TransactionDao transactionDao, PortfolioDao portfolioDao) {
        this.userInfoDao = userInfoDao;
        this.transactionDao = transactionDao;
        this.portfolioDao = portfolioDao;
    }

    /**
     * Method to buy a particular stock
     */
    @Transactional
    public ResponseEntity<TransactionOutputModel> buyStock(TransactionInputModel transactionEntity) {
        try {
            // Fetch the user from the database
            UserInfo user = userInfoDao.getUserByEmail(transactionEntity.getEmail());

            // Calculate the cost
            double cost = transactionEntity.getPrice() * transactionEntity.getVolume();

            if (user.getBalance() < cost){
                return ResponseEntity.ok().body(new TransactionOutputModel(transactionEntity.getStockName(), 0));
            }

            log.info(user.getEmail());
            // Update user's balance
            double newBalance = user.getBalance() - cost;
            user.setBalance(newBalance);
            userInfoDao.updateUserDetails(user);

            log.info(user.getEmail());

            // Create a transaction record
            TransactionEntity newTransaction = new TransactionEntity(transactionEntity.getEmail(), transactionEntity.getStockName(),
                    transactionEntity.getSymbol(), LocalTime.now(), LocalDate.now(), transactionEntity.getVolume(),
                    transactionEntity.getPrice(), transactionEntity.getVolume()* transactionEntity.getPrice(), "buy");
            transactionDao.createTransaction(newTransaction);

            // Update Portfolio table
            PortfolioEntity existingPortfolio = portfolioDao.findByEmailAndSymbol(transactionEntity.getEmail(), transactionEntity.getSymbol());
            PortfolioEntity newPortfolio = new PortfolioEntity(transactionEntity.getEmail(), transactionEntity.getStockName(),
                    transactionEntity.getSymbol(), transactionEntity.getVolume());
            if (existingPortfolio == null) {
                portfolioDao.createPortfolioEntry(newPortfolio);
            } else {
                portfolioDao.updatePortfolioEntry(existingPortfolio, transactionEntity.getVolume());
            }

            return ResponseEntity.ok(new TransactionOutputModel(newTransaction.getStockName(), newTransaction.getVolume()));
        } catch(Exception e){
            log.error("Error while trying to buy the stock", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new TransactionOutputModel(null,0));
        }
    }

    /**
     * Method to sell a particular stock
     */
    @Transactional
    public ResponseEntity<TransactionOutputModel> sellStock(TransactionInputModel transactionEntity) {
        try {
            // Fetch the user from the database
            UserInfo user = userInfoDao.getUserByEmail(transactionEntity.getEmail());

            // Calculate the cost
            double cost = transactionEntity.getPrice() * transactionEntity.getVolume();

            log.info(user.getEmail());
            // Update user's balance
            double newBalance = user.getBalance() + cost;
            user.setBalance(newBalance);
            userInfoDao.updateUserDetails(user);

            log.info(user.getEmail());

            // Create a transaction record
            TransactionEntity newTransaction = new TransactionEntity(transactionEntity.getEmail(), transactionEntity.getStockName(),
                    transactionEntity.getSymbol(), LocalTime.now(), LocalDate.now(), transactionEntity.getVolume(),
                    transactionEntity.getPrice(), transactionEntity.getVolume()* transactionEntity.getPrice(), "sell");
            transactionDao.createTransaction(newTransaction);

            // Update Portfolio table
            PortfolioEntity existingPortfolio = portfolioDao.findByEmailAndSymbol(transactionEntity.getEmail(), transactionEntity.getSymbol());
            if (existingPortfolio.getVolume() == transactionEntity.getVolume()){
                portfolioDao.deletePortfolioEntry(existingPortfolio);
            } else{
                portfolioDao.updatePortfolioEntry(existingPortfolio, -1*transactionEntity.getVolume());
            }

            return ResponseEntity.ok(new TransactionOutputModel(newTransaction.getStockName(), newTransaction.getVolume()));
        } catch(Exception e){
            log.error("Error while trying to sell the stock", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new TransactionOutputModel(null,0));
        }
    }

    /**
     * Method to fetch all the transactions of a user
     */
    public ResponseEntity<List<TransactionHistoryModel>> getTransactionHistory(String email){
        try {
            List<TransactionEntity> transactionEntityList = transactionDao.getTransactionByEmail(email);
            log.info(String.valueOf(transactionEntityList.size()));
            List<TransactionHistoryModel> transactionHistoryModelList = new ArrayList<>();
            for (TransactionEntity transaction : transactionEntityList) {
                transactionHistoryModelList.add(new TransactionHistoryModel(transaction.getStockName(), transaction.getSymbol(),
                        transaction.getDate(), transaction.getVolume(), transaction.getPrice(), transaction.getCost(),
                        transaction.getTransactionType()));
            }
            return ResponseEntity.ok(transactionHistoryModelList);
        } catch (Exception e){
            log.error("Error while fetching Transaction History");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
