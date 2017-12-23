package org.flush.rates.logic;

import org.flush.rates.model.Currency;
import org.flush.rates.model.Period;
import org.flush.rates.util.Util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExponentialRegression {

    private List<Period> periods = new ArrayList<>();
    private String currency;

    public List<Integer> xList = new ArrayList<>();

    public List<Double> inYList = new ArrayList<>();
    public List<Integer> x2List = new ArrayList<>();
    public List<Double> inY2List = new ArrayList<>();
    public List<Double> xInYList = new ArrayList<>();

    public List<Double> yMinusAvgYList = new ArrayList<>();
    public List<Double> expList = new ArrayList<>();
    public List<Double> yMinusExpList = new ArrayList<>();

    private List<LocalDate> nextPeriods = new ArrayList<>();

    public ExponentialRegression(String currency, List<Currency> currencyList) {
        this.currency = currency;
        toPeriod(currencyList, nextPeriods);
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

    public void calculateX() {
        for (int i = 1; i <= 12; i++)
            xList.add(i);
        xList.add(calculateLastInteger(xList));
    }

    public Double calculateLastDouble(List<Double> list) {
        double sum = 0;
        for (double number : list)
            sum += number;
        return sum;
    }
    public Integer calculateLastInteger(List<Integer> list) {
        int sum = 0;
        for (int number : list)
            sum += number;
        return sum;
    }

    public void calculateInY() {
        for(Period p : periods)
            inYList.add(Math.log(p.getValue()));
        //periods.forEach(item-> inYList.add(Math.log(item.getValue())));
        inYList.add(calculateLastDouble(inYList));

    }

    public void calculateX2() {
        for (int i = 1; i <= 12; i++)
            x2List.add(i * i);
        x2List.add(calculateLastInteger(x2List));
    }

    public void calculateInY2() {
        for (Double d : inYList)
            inY2List.add(d * d);
        inY2List.remove(inYList.size() - 1);
        inY2List.add(calculateLastDouble(inY2List));
    }

    public void calculateXInYList() {
        for (int i = 1; i <= x2List.size(); i++)
            xInYList.add(i * inYList.get(i - 1));
        xInYList.remove(xInYList.size() - 1);
        xInYList.add(calculateLastDouble(xInYList));
    }


    public double calculateEquations() {
        double n = xList.size() - 1;
        double xSum = xList.get(xList.size() - 1);
        double ySum = inYList.get(inYList.size() - 1);

        double secondXSum = xSum;
        double x2Sum = x2List.get(x2List.size() - 1);
        double xInYSum = xInYList.get(xInYList.size() - 1);

        double divider = (xSum / n) * -1; // value for multiply first equation

        n *= divider;
        xSum *= divider;
        ySum *= divider;

        secondXSum += n;
        x2Sum += xSum;
        xInYSum += ySum;

        return xInYSum / x2Sum;
    }

    public double calculateA(double b) {
        b *= xList.get(xList.size() - 1);
        double sum = inYList.get(inYList.size() - 1) - b;
        return sum / (xList.size() - 1);
    }

    public double calculateExpRegression(double a) {
        return Math.exp(a);
    }


    public void calculateYMinusAvgY() {
        double avgY = (periods.stream().mapToDouble(Period::getValue).sum()) / (periods.size());
        for (Period p : periods) {
            double value = p.getValue();
            yMinusAvgYList.add((value - avgY) * (value - avgY));
        }
        //periods.forEach(item -> yMinusAvgYList.add(Math.pow(item.getValue() - avgY, 2)));
    }

    public double calculateYMinusYAvgSum() {
        double sum = 0;
        for (double d : yMinusAvgYList)
            sum += d;
        return sum;
    }

    public void calculateExp(double expRegressionValue, double b) {
        for (int i = 1; i <= periods.size(); i++)
            expList.add(expRegressionValue * Math.exp(b * i));
        //expList.forEach(System.out::println);
    }

    public void calculateYMinusExp2() {
        for (int i = 0; i < periods.size(); i++) {
            yMinusExpList.add(Math.pow(periods.get(i).getValue() - expList.get(i), 2));
        }
    }

    public double calculateYMinusExp2Sum() {
        double sum = 0;
        for (double d : yMinusExpList)
            sum += d;
        return sum;
    }

    public double determinationCoefficient() {
        return 1 - (calculateYMinusExp2Sum() / calculateYMinusYAvgSum());
    }

    public List<Period> predictForNextMonths(double a, double b) {
        double expRegressionValue = calculateExpRegression(a);

        periods.add(new Period(nextPeriods.get(0).getMonth() + "-" + nextPeriods.get(0).getYear(), expRegressionValue * Math.exp(b * 13)));
        periods.add(new Period(nextPeriods.get(1).getMonth() + "-" + nextPeriods.get(1).getYear(), expRegressionValue * Math.exp(b * 14)));
        periods.add(new Period(nextPeriods.get(2).getMonth() + "-" + nextPeriods.get(2).getYear(), expRegressionValue * Math.exp(b * 15)));
        return periods;
    }

}
