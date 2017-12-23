package org.flush.rates.logic;

import org.flush.rates.model.Currency;
import org.flush.rates.model.Period;
import org.flush.rates.util.Util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PowerRegression {

    private List<Period> periods = new ArrayList<>();

    public List<Integer> xList = new ArrayList<>();
    public List<Double> inXList = new ArrayList<>();
    public List<Double> inX2List = new ArrayList<>();
    public List<Double> inYList = new ArrayList<>();
    public List<Double> inY2List = new ArrayList<>();
    public List<Double> inXinYList = new ArrayList<>();
    public List<Double> yMinusAvgYList = new ArrayList<>();
    public List<Double> expList = new ArrayList<>();
    public List<Double> yMinusExp2List = new ArrayList<>();
    private List<LocalDate> nextPeriods = new ArrayList<>();

    private String currency;

    public PowerRegression(String currency, List<Currency> currencyList) {
        this.currency = currency;
        toPeriod(currencyList, nextPeriods);

        //periods.forEach(System.out::println);
    }



    //calculate avg for each month
    private void toPeriod(List<Currency> currencyList, List<LocalDate> nextPeriods) {
        Util.fakeList(periods, currency);
        LocalDate date = null;
        double sum = 0;
        for (Currency currency : currencyList) {
            date = Util.getLocalDate(currency.getDate());
            if (date.getDayOfMonth() == date.lengthOfMonth()) {
                sum += Double.parseDouble(currency.getPrice());
                periods.add(new Period(date.getMonth() + "-" + date.getYear(), (sum / date.lengthOfMonth())));
                sum = 0;
            } else {
                sum += Double.parseDouble(currency.getPrice());
            }
        }

        if (date != null) {
            if (date.getDayOfMonth() != date.lengthOfMonth())
                periods.add(new Period(date.getMonth() + "-" + date.getYear(), sum / date.getDayOfMonth()));
            nextPeriods.add(date.plusMonths(1));
            nextPeriods.add(date.plusMonths(2));
            nextPeriods.add(date.plusMonths(3));

        }
    }


    public void writeXList() {
        for (int i = 1; i <= 12; i++) xList.add(i);
    }

    public void calculateInXList() {
        for (Integer i : xList)
            inXList.add(Math.log(i));
        //xList.forEach(item -> inXList.add(Math.log(item)));
    }

    public void calculateInY() {
        for (Period p : periods)
            inYList.add(Math.log(p.getValue()));
        //periods.forEach(item -> inYList.add(Math.log(item.getValue())));
    }

    public void calculateInX2List() {
        for (Double d : inXList)
            inX2List.add(d * d);
        //inXList.forEach(item -> inX2List.add(Math.pow(item, 2)));
    }

    public void calculateInY2List() {
        for (Double d : inYList)
            inY2List.add(d * d);
        //inYList.forEach(item -> inY2List.add(Math.pow(item, 2)));
    }

    public void calculateInXInYList() {
        for (int i = 0; i < inXList.size(); i++)
            inXinYList.add(inXList.get(i) * inYList.get(i));
    }

    public Integer calculateIntegerSum(List<Integer> list) {
        int sum = 0;
        for (Integer d : list)
            sum += d;
        return sum;
    }

    public Double calculateDoubleSum(List<Double> list) {
        double sum = 0;
        for (Double d : list)
            sum += d;
        return sum;
    }

    public double calculateB() {
        double n = xList.size();

        double inXSum = calculateDoubleSum(inXList);
        double inXSecondSum = inXSum;

        double inYSum = calculateDoubleSum(inYList);
        double inX2Sum = calculateDoubleSum(inX2List);
        double inY2Sum = calculateDoubleSum(inY2List);
        double inXinYSum = calculateDoubleSum(inXinYList);

        double divider = (inXSum / n) * -1;

        // Multiply
        n *= divider;
        inXSum *= divider;
        inYSum *= divider;

        inXSecondSum += n;
        inX2Sum += inXSum;
        inXinYSum += inYSum;

        return inXinYSum / inX2Sum;
    }

    public double calculateA(double b) {
        double n = xList.size();
        double inXSum = calculateDoubleSum(inXList);
        double inYSum = calculateDoubleSum(inYList);
        b *= inXSum;

        return  (inYSum - b) / n;
    }

    public double calculatePowerRegressionValue(double a) {
        return Math.exp(a);
    }

    public double calculateYAvg() {
        double avg = 0;
        for (Period p : periods)
            avg += p.getValue();
        return avg / periods.size();
    }

    public void calculateYMinusAvgY() {
        double avg = calculateYAvg();
        for (Period p : periods)
            yMinusAvgYList.add(Math.pow(p.getValue() - avg, 2));
        //periods.forEach(item -> yMinusAvgYList.add(Math.pow(item.getValue() - avg, 2)));
    }

    public double calculateYMinusAvgYSum() {
        double sum = 0;
        for (Double p : yMinusAvgYList)
            sum += p;
        return sum;
    }


    public void calculateExp(double regression, double b) {
        for (Integer i: xList)
            expList.add(regression * Math.pow(i, b));
        //xList.forEach(item -> expList.add(regression * Math.pow(item, b)));
    }

    public void calculateYMinusExp2() {
        for (int i = 0; i < periods.size(); i++)
            yMinusExp2List.add(Math.pow(periods.get(i).getValue() - expList.get(i), 2));
    }

    public double calculateExp2Sum() {
        double sum = 0;
        for (Double d : yMinusExp2List)
            sum += d;
        return sum;
    }

    public double calculateDeterminationCoefficient() {
        return 1 - (calculateExp2Sum() / calculateYMinusAvgYSum());
    }

    public List<Period> predictForNextMonths(double a, double b) {
        double powerRegressionValue = calculatePowerRegressionValue(a);
        periods.add(new Period(nextPeriods.get(0).getMonth() + "-" + nextPeriods.get(0).getYear(), powerRegressionValue * Math.pow(13, b)));
        periods.add(new Period(nextPeriods.get(1).getMonth() + "-" + nextPeriods.get(1).getYear(), powerRegressionValue * Math.pow(14, b)));
        periods.add(new Period(nextPeriods.get(2).getMonth() + "-" + nextPeriods.get(2).getYear(), powerRegressionValue * Math.pow(15, b)));
        return periods;
    }


}
