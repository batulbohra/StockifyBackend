package com.progsa.IOModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PortfolioOutputModel {
    private String email;
    private String stockName;
    private String symbol;
    private double curPrice;
    private int curVolume;
    private double curCost;
    private double percentagePriceChange;
}
