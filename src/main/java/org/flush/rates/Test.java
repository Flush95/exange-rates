package org.flush.rates;


import org.flush.rates.logic.ExponentialRegression;
import org.flush.rates.logic.PowerRegression;
import org.flush.rates.network.Connection;
import org.flush.rates.util.JSONParsing;
import org.flush.rates.util.Util;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        /*ExponentialRegression exponentialRegression = new ExponentialRegression("EUR");
        exponentialRegression.fakeList();
        exponentialRegression.calculateX();
        exponentialRegression.calculateInY();
        exponentialRegression.calculateX2();
        exponentialRegression.calculateInY2();
        exponentialRegression.calculateXInYList();


        exponentialRegression.calculateYMinusAvgY();
        exponentialRegression.calculateExp(exponentialRegression.calculateExpRegression(exponentialRegression.calculateA(exponentialRegression.calculateEquations())),
                exponentialRegression.calculateEquations());
        exponentialRegression.calculateYMinusExp2();

        System.out.println("Determination coefficient: " + exponentialRegression.determinationCoefficient());*/


       /* PowerRegression powerRegression = new PowerRegression("EUR");
        powerRegression.fakeList();

        powerRegression.writeXList();
        powerRegression.calculateInXList();
        powerRegression.calculateInY();
        powerRegression.calculateInX2List();
        powerRegression.calculateInY2List();
        powerRegression.calculateInXInYList();



        double b = powerRegression.calculateB();
        double regression = powerRegression.calculatePowerRegressionValue(powerRegression.calculateA(b));

        powerRegression.calculateYMinusAvgY();

        System.out.println(powerRegression.calculateYMinusAvgYSum());
        powerRegression.calculateExp(regression, b);
        powerRegression.calculateYMinusExp2();
        powerRegression.yMinusExp2List.forEach(System.out::println);

        System.out.println(powerRegression.calculateDeterminationCoefficient());*/


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        LocalDate localDate = Util.getLocalDate(dtf.format(now));

        /*String endDate = localDate.toString();
        localDate = localDate.minusDays(localDate.getDayOfMonth() - 1);

        String firstDate = localDate.toString();*/

        for (JSONObject j : new Connection().predictionsRequests(localDate.minusDays(localDate.getDayOfMonth() - 1), localDate.plusDays(5), "EUR", "UAH")) {
            System.out.println(j);
        }

    }


}
