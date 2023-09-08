package com.progsa.dao;

import com.progsa.model.PortfolioEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PortfolioDaoImpl implements PortfolioDao {

    private final PortfolioRepository portfolioRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public PortfolioDaoImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
        this.entityManager = entityManager;
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

    @Override
    @Transactional
    public void deletePortfolioEntry(PortfolioEntity portfolioEntry) {
        // Check if the entity is managed (attached to the persistence context)
        if (!entityManager.contains(portfolioEntry)) {
            // If it's not managed, merge it to attach it
            portfolioEntry = entityManager.merge(portfolioEntry);
        }
        // Delete the entity
        entityManager.remove(portfolioEntry);
    }
}
