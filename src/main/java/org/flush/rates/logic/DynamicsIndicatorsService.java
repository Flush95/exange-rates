package org.flush.rates.logic;

import org.flush.rates.model.Currency;
import org.flush.rates.model.Period;
import org.flush.rates.util.Util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DynamicsIndicatorsService {

    private List<Period> periods = new ArrayList<>();
    private List<LocalDate> nextPeriods = new ArrayList<>();

    //Dynamics table's lists
    private List<Double> absIncrease = new ArrayList<>();
    private List<Double> rateOfIncrease = new ArrayList<>();
    private List<Double> ratesOfGrowth = new ArrayList<>();
    private List<Double> absContent = new ArrayList<>();
    private List<Double> rateOfBuildUp = new ArrayList<>();

    //Basics table's lists
    private List<Double> basicsAbsoluteIncrease = new ArrayList<>();
    private List<Double> basicsRateOfIncrease = new ArrayList<>();
    private List<Double> basicsRateOfGrowth = new ArrayList<>();

    public List<Period> getPeriods() {
        return periods;
    }
    private String currency;

    public DynamicsIndicatorsService(String currency) {
        this.currency = currency;
    }

    //needed list





    /*** DYNAMICS INDICATORS ***/
    public List<Period> predict() {
        double d = calculateAvgGrowthRate();

        double first = (periods.get(periods.size() - 1).getValue() * d);
        double second = (first * d);
        double third = (second * d);

        periods.add(new Period(nextPeriods.get(0).getMonth().toString() + "-" + nextPeriods.get(0).getYear(), first));
        periods.add(new Period(nextPeriods.get(1).getMonth().toString() + "-" + nextPeriods.get(1).getYear(), second));
        periods.add(new Period(nextPeriods.get(2).getMonth().toString() + "-" + nextPeriods.get(2).getYear(), third));

        for (Period p : periods)
            System.out.println(p);
        return periods;
    }

    //Средний уровень интервального ряда
    public double calculateAvgLevelOfDynamicsSeries() {
        double sum = 0;
        for (Period p : periods)
            sum += p.getValue();
        return sum / periods.size();
    }

    //Средний темп роста
    public double calculateAvgGrowthRate() {
        return Util.round(Math.pow((periods.get(periods.size() - 1).getValue() / periods.get(0).getValue()), (1.0 / (periods.size() - 1))), 4);
    }

    //Средний темп прироста
    public double calculateAvgIncreaseRate(double avgGrowthRate) {
        return Util.round((avgGrowthRate - 1), 4) * 100;
    }

    //Средний абсолютный прирост
    public double calculateAvgAbsoluteIncrease() {
        int p = periods.size() - 1;
        return ((periods.get(periods.size() - 1).getValue() - periods.get(0).getValue()) / p);
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


    /** DYNAMIC TABLE CALCULATION AND METHODS **/
    public void calculateDynamicsTable(List<Currency> currencyList) {
        toPeriod(currencyList, nextPeriods);
        calculateAbsIncrease(periods);
        calculateRateOfIncrease(periods);
        calculateRatesOfGrowth(periods);
        calculateAbsContent(periods);
        calculateRateOfBuildUp(periods);
    }

    //Абсолютный прирост
    private void calculateAbsIncrease(List<Period> periods) {
        absIncrease.add((double)0);
        for (int i = 1; i < periods.size(); i++)
            absIncrease.add(periods.get(i).getValue() - periods.get(i - 1).getValue());
    }

    //Темп прироста %
    private void calculateRateOfIncrease(List<Period> periods) {
        rateOfIncrease.add((double) 0);
        for (int i = 1; i < periods.size(); i++) {
            double rate = ((periods.get(i).getValue() - periods.get(i - 1).getValue()) / periods.get(i - 1).getValue()) * 100.0;
            rateOfIncrease.add(rate);
        }
    }

    //Темпы роста %
    private void calculateRatesOfGrowth(List<Period> periods) {
        ratesOfGrowth.add((double) 100);
        for (int i = 1; i < periods.size(); i++) {
            ratesOfGrowth.add((periods.get(i).getValue() / periods.get(i - 1).getValue()) * 100);
        }
    }

    //Абсолютное содержание 1% прироста
    private void calculateAbsContent(List<Period> periods) {
        absContent.add((double) 0);
        for (int i = 1; i < periods.size(); i++) {
            absContent.add(periods.get(i - 1).getValue() / 100);
        }
    }

    //Темп наращения %
    private void calculateRateOfBuildUp(List<Period> periods) {
        rateOfBuildUp.add((double) 0);
        for (int i = 1; i < absIncrease.size(); i++) {
            rateOfBuildUp.add(((periods.get(i).getValue() - periods.get(i - 1).getValue()) / periods.get(0).getValue()) * 100);
        }
    }
    /** ******************** **/



    /** BASIC TABLE DYNAMICS INDICES **/

    public void calculateBasicDynamics() {
        calculateBasicsAbsoluteIncrease();
        calculateBasicsRateOfIncrease();
        calculateBasicsRateOfGrowth();
    }

    //Абсолютный прирост
    public void calculateBasicsAbsoluteIncrease() {
        for (Period p : periods)
            basicsAbsoluteIncrease.add(p.getValue() - periods.get(0).getValue());
    }

    //Темп прироста в %
    public void calculateBasicsRateOfIncrease() {
        for (int i = 0; i < basicsAbsoluteIncrease.size(); i++)
            basicsRateOfIncrease.add((basicsAbsoluteIncrease.get(i) / periods.get(0).getValue()) * 100);
    }

    //Темпы роста %
    public void calculateBasicsRateOfGrowth() {
        for (double d : basicsAbsoluteIncrease)
            basicsRateOfGrowth.add(((d / periods.get(0).getValue()) * 100) + 100);
    }

    /** ******************** **/
}
