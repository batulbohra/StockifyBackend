package com.progsa.IOModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockDetailOutputModel {
    String email;
    String symbol;
    double netProfitLoss;
    long curVolume;
    double curPrice;
    double curCost;
}
