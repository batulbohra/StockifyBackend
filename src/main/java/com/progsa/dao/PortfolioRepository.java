package com.progsa.dao;

import com.progsa.model.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {
    List<PortfolioEntity> findByEmail(String email);
    PortfolioEntity findByEmailAndSymbol(String email, String symbol);
}
