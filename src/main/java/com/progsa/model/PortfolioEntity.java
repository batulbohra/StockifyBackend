package com.progsa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "portfolio")
@Getter
@Setter
@NoArgsConstructor
public class PortfolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String stockName;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private int volume;

    public PortfolioEntity(String email, String stockName, String symbol, int volume) {
        this.email = email;
        this.stockName = stockName;
        this.symbol = symbol;
        this.volume = volume;
    }
}
