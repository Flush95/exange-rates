package org.flush.rates.util;

import org.flush.rates.model.Currency;
import org.flush.rates.model.Period;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static List<String> rates = new ArrayList<>();

    static {
        rates.add("UAH");
        rates.add("EUR");
        rates.add("AUD");
        rates.add("BGN");
        rates.add("BRL");
        rates.add("CAD");
        rates.add("CHF");
        rates.add("CNY");
        rates.add("CZK");
        rates.add("DKK");
        rates.add("GBP");
        rates.add("HKD");
        rates.add("HRK");
        rates.add("HUF");
        rates.add("IDR");
        rates.add("ILS");
        rates.add("INR");
        rates.add("JPY");
        rates.add("KRW");
        rates.add("MXN");
        rates.add("MYR");
        rates.add("NOK");
        rates.add("NZD");
        rates.add("PHP");
        rates.add("PLN");
        rates.add("RON");
        rates.add("RUB");
        rates.add("SEK");
        rates.add("SGD");
        rates.add("THB");
        rates.add("TRY");
        rates.add("USD");
        rates.add("ZAR");
    }

    public static void fakeList(List<Period> periods, String currency) {
        if (currency.equals("EUR")) {
            periods.add(new Period("JANUARY-2017", 28.893870967741925));
            periods.add(new Period("FEBRUARY-2017", 28.675000000000004));
            periods.add(new Period("MARCH-2017", 28.50403225806452));
            periods.add(new Period("APRIL-2017", 28.357499999999998));
            periods.add(new Period("MAY-2017", 28.65467741935484));
            periods.add(new Period("JUNE-2017", 28.904999999999994));
            periods.add(new Period("JULY-2017", 29.467741935483872));
            periods.add(new Period("AUGUST-2017", 29.91016129032258));
            periods.add(new Period("SEPTEMBER-2017", 30.846666666666668));
            periods.add(new Period("OCTOBER-2017", 31.129516129032258));
            periods.add(new Period("NOVEMBER-2017", 31.137333333333334));
            //periods.add(new Period("DECEMBER-2017", 31.973333333333333));
        } else if (currency.equals("USD")) {
            periods.add(new Period("JANUARY-2017", 27.46016129032258));
            periods.add(new Period("FEBRUARY-2017", 27.054999999999996));
            periods.add(new Period("MARCH-2017", 26.867419354838717));
            periods.add(new Period("APRIL-2017", 26.625166666666665));
            periods.add(new Period("MAY-2017", 26.181290322580647));
            periods.add(new Period("JUNE-2017", 25.922166666666666));
            periods.add(new Period("JULY-2017", 25.799032258064518));
            periods.add(new Period("AUGUST-2017", 25.53177419354839));
            periods.add(new Period("SEPTEMBER-2017", 26.0265));
            periods.add(new Period("OCTOBER-2017", 26.58290322580646));
            periods.add(new Period("NOVEMBER-2017", 26.646500000000003));
            //periods.add(new Period("DECEMBER-2017", 27.167777777777783));
        } else if (currency.equals("GBP")) {
            periods.add(new Period("JANUARY-2017", 32.6233870967742));
            periods.add(new Period("FEBRUARY-2017", 32.902678571428574));
            periods.add(new Period("MARCH-2017", 32.314516129032256));
            periods.add(new Period("APRIL-2017", 32.58616666666667));
            periods.add(new Period("MAY-2017", 32.77290322580645));
            periods.add(new Period("JUNE-2017", 32.31983333333333));
            periods.add(new Period("JULY-2017", 32.70887096774194));
            periods.add(new Period("AUGUST-2017", 32.38741935483872));
            periods.add(new Period("SEPTEMBER-2017", 33.46633333333333));
            periods.add(new Period("OCTOBER-2017", 34.21354838709678));
            periods.add(new Period("NOVEMBER-2017", 34.3635));
            //periods.add(new Period("DECEMBER-2017", 35.60722222222222));
        } else if (currency.equals("RUB")) {
            periods.add(new Period("JANUARY-2017", 0.43585483870967734));
            periods.add(new Period("FEBRUARY-2017", 0.4450178571428571));
            periods.add(new Period("MARCH-2017", 0.4423322580645159));
            periods.add(new Period("APRIL-2017", 0.4166766666666667));
            periods.add(new Period("MAY-2017", 0.39122580645161287));
            periods.add(new Period("JUNE-2017", 0.41934999999999995));
            periods.add(new Period("JULY-2017", 0.38201612903225823));
            periods.add(new Period("AUGUST-2017", 0.37082258064516144));
            periods.add(new Period("SEPTEMBER-2017", 0.37082258064516144));
            periods.add(new Period("OCTOBER-2017", 0.38841935483870965));
            periods.add(new Period("NOVEMBER-2017", 0.39655));
            //periods.add(new Period("DECEMBER-2017", 0.3936388888888888));
        }
    }



    public static List<Currency> filterResultList(List<Currency> oldList, String symbols) {
        List<Currency> newList = new ArrayList<>();
        if (symbols == null) return oldList;
        String[] symbolsArray = symbols.split(",");
        for (String symbol : symbolsArray) {
            for (Currency currency : oldList) {
                if (currency.getToCurrency().equals(symbol))
                    newList.add(currency);
            }
        }
        return newList;
    }

    public static List<Currency> filterOne(List<Currency> oldList, String to) {
        List<Currency> newList = new ArrayList<>();
        for (Currency currency : oldList)
            if (currency.getToCurrency().equals(to))
                newList.add(currency);

        return newList;
    }



    public static boolean checkDiapason(String startDate, String endDate) {
        String start[] = startDate.split("-");
        String end[] = endDate.split("-");
        LocalDate lStartDate = LocalDate.of(Integer.valueOf(start[0]), Integer.valueOf(start[1]), Integer.valueOf(start[2]));
        LocalDate localEndDate = LocalDate.of(Integer.valueOf(end[0]), Integer.valueOf(end[1]), Integer.valueOf(end[2]));
        return lStartDate.isBefore(localEndDate);
    }

    public static LocalDate getLocalDate(String date) {
        String start[] = date.split("-");
        return LocalDate.of(Integer.valueOf(start[0]), Integer.valueOf(start[1]), Integer.valueOf(start[2]));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
