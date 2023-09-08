package com.progsa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "portfolio")
@Getter
@Setter
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
}
