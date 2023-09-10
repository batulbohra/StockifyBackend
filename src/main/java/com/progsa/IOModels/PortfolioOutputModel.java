package com.progsa.IOModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PortfolioOutputModel {
    private double netPortfolioGain;
    private double netPortfolioValue;
    private List<PortfolioStockModel> portfolioStockModelList;
}
