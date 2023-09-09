package com.progsa.service;

import com.progsa.IOModels.TransactionInputModel;
import com.progsa.IOModels.TransactionOutputModel;
import com.progsa.dao.PortfolioDao;
import com.progsa.dao.TransactionDaoImpl;
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

import java.util.Date;

@Service
@Slf4j
public class TransactionService {

    private final UserInfoDao userInfoDao;
    private final TransactionDaoImpl transactionDaoImpl;

    private final PortfolioDao portfolioDao;

    @Autowired
    public TransactionService(UserInfoDao userInfoDao, TransactionDaoImpl transactionDaoImpl, PortfolioDao portfolioDao) {
        this.userInfoDao = userInfoDao;
        this.transactionDaoImpl = transactionDaoImpl;
        this.portfolioDao = portfolioDao;
    }

    @Transactional
    public ResponseEntity<TransactionOutputModel> buyStock(TransactionInputModel transactionEntity) {
        try {
            // Fetch the user from the database
            UserInfo user = userInfoDao.getUserByEmail(transactionEntity.getEmail());

            // Calculate the cost
            double cost = transactionEntity.getPrice() * transactionEntity.getVolume();

            log.info(user.getEmail());
            // Update user's balance
            double newBalance = user.getBalance() - cost;
            user.setBalance(newBalance);
            userInfoDao.updateUserDetails(user);

            log.info(user.getEmail());

            // Create a transaction record
            TransactionEntity newTransaction = new TransactionEntity(transactionEntity.getEmail(), transactionEntity.getStockName(),
                    transactionEntity.getSymbol(), new Date(), new Date(), transactionEntity.getVolume(),
                    transactionEntity.getPrice(), transactionEntity.getVolume()* transactionEntity.getPrice(), "buy");
            transactionDaoImpl.createTransaction(newTransaction);

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
            log.error("Service Error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new TransactionOutputModel(null,0));
        }
    }

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
                    transactionEntity.getSymbol(), new Date(), new Date(), transactionEntity.getVolume(),
                    transactionEntity.getPrice(), transactionEntity.getVolume()* transactionEntity.getPrice(), "sell");
            transactionDaoImpl.createTransaction(newTransaction);

            // Update Portfolio table
            PortfolioEntity existingPortfolio = portfolioDao.findByEmailAndSymbol(transactionEntity.getEmail(), transactionEntity.getSymbol());
            if (existingPortfolio.getVolume() == transactionEntity.getVolume()){
                portfolioDao.deletePortfolioEntry(existingPortfolio);
            } else{
                portfolioDao.updatePortfolioEntry(existingPortfolio, -1*transactionEntity.getVolume());
            }

            return ResponseEntity.ok(new TransactionOutputModel(newTransaction.getStockName(), newTransaction.getVolume()));
        } catch(Exception e){
            log.error("Service Error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new TransactionOutputModel(null,0));
        }
    }
}
