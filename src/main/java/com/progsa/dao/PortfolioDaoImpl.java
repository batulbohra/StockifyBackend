package com.progsa.dao;

import com.progsa.model.PortfolioEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PortfolioDaoImpl implements PortfolioDao {

    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioDaoImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public List<PortfolioEntity> findByEmail(String email) {
        return portfolioRepository.findByEmail(email);
    }

    @Override
    public PortfolioEntity findByEmailAndSymbol(String email, String symbol) {
        return portfolioRepository.findByEmailAndSymbol(email, symbol);
    }

    @Override
    public void createPortfolioEntry(PortfolioEntity portfolioEntry) {
        portfolioRepository.save(portfolioEntry);
    }

    @Override
    public void updatePortfolioEntry(PortfolioEntity existingPortfolioEntry, int additionalVolume) {
        int olderVolume = existingPortfolioEntry.getVolume();
        existingPortfolioEntry.setVolume(olderVolume+additionalVolume);
        // Save the updated entity back to the database
        portfolioRepository.save(existingPortfolioEntry);
        }
    }
