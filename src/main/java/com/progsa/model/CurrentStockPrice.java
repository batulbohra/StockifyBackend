package com.progsa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrentStockPrice {
    @JsonProperty("c")
    private double currentPrice;
    @JsonProperty("l")
    private double lowPrice;
    @JsonProperty("h")
    private double highPrice;
    @JsonProperty("o")
    private double openingPrice;
}
