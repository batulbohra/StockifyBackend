package com.progsa.IOModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockDetailInputModel {
    @NonNull
    private String email;
    @NonNull
    private String stockName;
    @NonNull
    private String symbol;
}
