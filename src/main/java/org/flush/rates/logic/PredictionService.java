package org.flush.rates.logic;

import org.flush.rates.model.Period;

import java.util.List;

public class PredictionService {
    private List<Period> periods;
    private String date;
    private String baseCurrency;

    public PredictionService(String baseCurrency, String date) {
        this.date = date;
        this.baseCurrency = baseCurrency;
    }



}
