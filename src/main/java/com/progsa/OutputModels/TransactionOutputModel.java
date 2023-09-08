package com.progsa.OutputModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class TransactionOutputModel {
    private String stockName;
    private int volume;
}
