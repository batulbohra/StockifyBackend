package com.progsa.service;

import com.progsa.Util;
import com.progsa.dao.PortfolioDao;
import com.progsa.dao.TransactionDaoImpl;
import com.progsa.dao.UserInfoDao;
import com.progsa.model.PortfolioEntity;
import com.progsa.model.TransactionEntity;
import com.progsa.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.progsa.Constants.INSUFFICIENT_FUNDS;
import static com.progsa.Constants.USER_NOT_FOUND;

@Service
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
    public ResponseEntity<String> buyStock(String email, String stockName, String symbol, double price, int volume) {
        // Fetch the user from the database
        UserInfo user = userInfoDao.getUserByEmail(email);

        // Calculate the cost
        double cost = price * volume;

        // Update user's balance
        double newBalance = user.getBalance() - cost;
        user.setBalance(newBalance);
        userInfoDao.updateUserDetails(user);

        // Create a transaction record
        TransactionEntity transaction = Util.createTransactionEntity(email,stockName,symbol,price,volume,"buy");
        transactionDaoImpl.createTransaction(transaction);

        // Add to the portfolio if net summation is 0 or less
        PortfolioEntity existingPortfolio = portfolioDao.findByEmailAndSymbol(email, symbol);
        PortfolioEntity newPortfolio = Util.createPortfolioEntity(email, stockName, symbol, volume);
        if (existingPortfolio==null) {
            portfolioDao.createPortfolioEntry(newPortfolio);
        } else{
            // Check net summation directly from the database
            portfolioDao.updatePortfolioEntry(existingPortfolio, volume);
        }

        String response = "stock name: "+stockName+" volume: "+volume;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
