package com.progsa.dao;

import com.progsa.model.PortfolioEntity;

import java.util.List;

public interface PortfolioDao {
    List<PortfolioEntity> findByEmail(String email);
    PortfolioEntity findByEmailAndSymbol(String email, String symbol);
    void createPortfolioEntry(PortfolioEntity portfolioEntry);
    void updatePortfolioEntry(PortfolioEntity portfolioEntry, int additionalVolume);
    void deletePortfolioEntry(PortfolioEntity portfolioEntity);
}
