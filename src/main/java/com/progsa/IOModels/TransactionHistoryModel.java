package com.progsa.IOModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class TransactionHistoryModel {
    private Long transactionId;
    private String stockName;
    private String symbol;
    private LocalDate date;
    private int volume;
    private double price;
    private double cost;
    private String transactionType;
}
