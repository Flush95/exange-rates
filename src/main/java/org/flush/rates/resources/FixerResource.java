package org.flush.rates.resources;

import org.flush.rates.errors.ErrorMessages;
import org.flush.rates.logic.Converter;
import org.flush.rates.logic.DynamicsIndicatorsService;
import org.flush.rates.logic.ExponentialRegression;
import org.flush.rates.logic.PowerRegression;
import org.flush.rates.model.Currency;
import org.flush.rates.model.Period;
import org.flush.rates.network.Connection;
import org.flush.rates.util.FormParamChecker;
import org.flush.rates.util.JSONParsing;
import org.flush.rates.util.Util;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Path("resource")
public class FixerResource {
    private static final JSONParsing jsonParsing = new JSONParsing();
    private FormParamChecker checker = new FormParamChecker();
    private Connection connection = new Connection();
    private Converter converter = new Converter();
    private JSONObject temp = null;

    @POST
    @Path("/uahLive")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Currency> getLiveUAH(@FormParam("liveSend") String currency, @FormParam("toCurrency") String toCurrency) {

        String baseCurrency = checker.formatCurrency(currency);

        if (!checker.isRate(baseCurrency))
            throw new WebApplicationException(ErrorMessages.baseCurrencyException());

        toCurrency = checker.formatCurrency(toCurrency);
        if (!checker.isRate(toCurrency))
            throw new WebApplicationException(ErrorMessages.rateException());

        List<Currency> toReturn = new ArrayList<>();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        LocalDate localDate = Util.getLocalDate(dtf.format(now));

        //localDate = localDate.minusDays(1);

        /*if (baseCurrency.equals("UAH")) {
            temp = connection.sendRequestToMinFin(null);
            toReturn.addAll(jsonParsing.parseFromMinFin(temp, null));
            temp = connection.sendRequestToMinFin(localDate.toString());
            toReturn.addAll(jsonParsing.parseFromMinFin(temp, null));
        } else if (!baseCurrency.equals("UAH")) {
            temp = connection.sendRequestToFixerIO(null, baseCurrency, null);
            toReturn.addAll(jsonParsing.parseFromFixerIO(temp));
            temp = connection.sendRequestToFixerIO(localDate.minusDays(1).toString(), baseCurrency, null);
            toReturn.addAll(jsonParsing.parseFromFixerIO(temp));
        }*/
        JSONObject object = connection.oneRequestToFreeCurrencyConverter(localDate.toString(), toCurrency, baseCurrency);


        toReturn.addAll(jsonParsing.parseOne(object, toCurrency, baseCurrency));

        object = connection.oneRequestToFreeCurrencyConverter(localDate.minusDays(1).toString(), toCurrency, baseCurrency);
        toReturn.addAll(jsonParsing.parseOne(object, toCurrency, baseCurrency));


        double difference = Double.parseDouble(toReturn.get(0).getPrice()) - Double.parseDouble(toReturn.get(1).getPrice());

        toReturn.remove(1);
        toReturn.get(0).setPrice(String.valueOf(Util.round(Double.parseDouble(toReturn.get(0).getPrice()), 3)));
        toReturn.get(0).setPriceSold(String.valueOf(Util.round(difference, 3)));
        toReturn.get(0).setToCurrency(toCurrency);

        return toReturn;
    }

    @POST
    @Path("/byDate")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Currency> getIt(@FormParam("inputDate") String date,
                                @FormParam("selectBaseCurrency") String currency,
                                @FormParam("currenciesByDate") String currenciesByDate) {

        String baseCurrency = checker.formatCurrency(currency);
        currenciesByDate = currenciesByDate.toUpperCase();
        if (!checker.isRate(baseCurrency))
            throw new WebApplicationException(ErrorMessages.baseCurrencyException());
        String symbols;
        if (!checker.checkSymbols(currenciesByDate)) symbols = null;
        else symbols = checker.formatSymbols(currenciesByDate);

        if (baseCurrency.equals("UAH")) {
            temp = connection.sendRequestToMinFin(date);
            return jsonParsing.parseFromMinFin(temp, date);
        } else if (!baseCurrency.equals("UAH")) {
            temp = connection.sendRequestToFixerIO(date, baseCurrency, symbols);
            return jsonParsing.parseFromFixerIO(temp);
        }
        throw new WebApplicationException(ErrorMessages.unknownException());
    }

    @POST
    @Path("/converter")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Currency> convert(@FormParam("converterCurrency") String currency,
                                  @FormParam("toRates") String toRate,
                                  @FormParam("converterDate") String date,
                                  @FormParam("inputQuantity") String quantity) {
        //validate base currency
        String baseCurrency = checker.formatCurrency(currency);
        if (!checker.isRate(baseCurrency))
            throw new WebApplicationException(ErrorMessages.baseCurrencyException());

        //validate to currency
        String to = checker.formatCurrency(toRate);
        if (!checker.isRate(to))
            throw new WebApplicationException(ErrorMessages.rateException());

        //validate quantity
        if (!checker.isNumeric(quantity))
            throw new WebApplicationException(ErrorMessages.numericException());

        if (date == null && baseCurrency.equals("UAH")) {
            temp = connection.sendRequestToConverter(baseCurrency, to);
            return converter.convert(Util.filterOne(jsonParsing.parseOneConverterRate(temp), to), quantity);
        }

        if (date == null && !baseCurrency.equals("UAH"))
            throw new WebApplicationException(ErrorMessages.dateException());

        if (to.equals("UAH")) {
            temp = connection.sendRequestToMinFin(date);
            return converter.convert(Util.filterOne(jsonParsing.parseFromMinFin(temp, date), baseCurrency), quantity);
        } else {
            temp = connection.sendRequestToFixerIO(date, baseCurrency, to);
            return converter.convert(jsonParsing.parseFromFixerIO(temp), quantity);
        }

        /*if (!date.isEmpty()) {
            if (baseCurrency.equals("UAH")) {
                temp = connection.sendRequestToMinFin(date);
                return converter.convert(Util.filterResultList(jsonParsing.parseFromMinFin(temp, date), symbols), quantity);
            } else if (!baseCurrency.equals("UAH")) {
                temp = connection.sendRequestToFixerIO(date, baseCurrency, symbols);
                return converter.convert(jsonParsing.parseFromFixerIO(temp), quantity);
            }
        }*/

        /*if (symbols == null) {
            temp = connection.sendRequestToConverter(baseCurrency, null);
            return converter.convert(jsonParsing.parseFromConverterAll(temp), quantity);
        } else if (checker.checkSymbols(symbols)) {
            if (symbols.trim().length() == 3) {
                temp = connection.sendRequestToConverter(baseCurrency, symbols);
                return converter.convert(jsonParsing.parseOneConverterRate(temp), quantity);
            }
            else if (symbols.length() >= 7) {
                temp = connection.sendRequestToConverter(baseCurrency, null);
                return converter.convert(jsonParsing.parseManyConverterRates(temp, symbols), quantity);
            }
        }*/
        //throw new WebApplicationException(ErrorMessages.unknownException());
    }


    @POST
    @Path("/hotChart")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Currency> buildHotChart(@FormParam("baseChartCurrency") String baseChartCurrency,
                                     @FormParam("toChartCurrency") String chartCurrency) {

        //validate base currency
        String baseCurrency = checker.formatCurrency(baseChartCurrency);
        System.out.println("Base: " + baseChartCurrency);
        if (!checker.isRate(baseCurrency))
            throw new WebApplicationException(ErrorMessages.baseCurrencyException());

        chartCurrency = checker.formatCurrency(chartCurrency);
        System.out.println(chartCurrency);
        if (!checker.isRate(chartCurrency.toUpperCase()))
            throw new WebApplicationException(ErrorMessages.symbolsException());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        LocalDate localDate = Util.getLocalDate(dtf.format(now));

        String endDate = localDate.toString();
        localDate = localDate.minusDays(6);
        String startDate = localDate.toString();

        //List<JSONObject> jsonObjects = connection.openDiapasonConnection(startDate, endDate, baseCurrency, chartCurrency.toUpperCase());
        JSONObject jsonObjects = connection.sendRequestToFreeCurrencyConverter(startDate, endDate, baseCurrency, chartCurrency);

        return jsonParsing.parse(jsonObjects, chartCurrency, baseCurrency);
    }

    @POST
    @Path("/chart")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Currency> buildChart(@FormParam("baseChartCurrency") String baseChartCurrency,
                                     @FormParam("toChartCurrency") String chartCurrency,
                                     @FormParam("firstChartDate") String startDate,
                                     @FormParam("secondChartDate") String endDate) {

        //validate base currency
        String baseCurrency = checker.formatCurrency(baseChartCurrency);
        if (!checker.isRate(baseCurrency))
            throw new WebApplicationException(ErrorMessages.baseCurrencyException());

        chartCurrency = checker.formatCurrency(chartCurrency);
        if (!checker.isRate(chartCurrency.toUpperCase()))
            throw new WebApplicationException(ErrorMessages.symbolsException());

        if (startDate.isEmpty() || endDate.isEmpty() || !Util.checkDiapason(startDate, endDate))
            throw new WebApplicationException(ErrorMessages.dateException());

        List<JSONObject> jsonObjects = connection.openDiapasonConnection(startDate, endDate, baseCurrency, chartCurrency.toUpperCase());

        return jsonParsing.parseDiapason(jsonObjects, baseCurrency, connection.getDates(), chartCurrency.toUpperCase());
    }

    @POST
    @Path("/dynamics")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Period> predictRatesValue(@FormParam("predictionCurrency") String toDynamicCurrency,
                                          @FormParam("typeOfPrediction") String algorithm) {

        if (algorithm == null || algorithm.isEmpty())
            throw new WebApplicationException(ErrorMessages.unknownException());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        LocalDate localDate = Util.getLocalDate(dtf.format(now));

        String endDate = localDate.toString();
        localDate = localDate.minusDays(localDate.getDayOfMonth() - 1);

        String firstDate = localDate.toString();
        //String firstDate = localDate.minusMonths(12).toString();

        System.out.println("Start date: " + firstDate);
        System.out.println("End date: " + endDate);
        List<JSONObject> jsonObjects = connection.openDiapasonConnection(firstDate, endDate, "UAH", toDynamicCurrency.toUpperCase());


        if (algorithm.equals("Dynamics Indicators")) {
            DynamicsIndicatorsService service = new DynamicsIndicatorsService(toDynamicCurrency.toUpperCase());
            service.calculateDynamicsTable(jsonParsing.parseDiapason(jsonObjects, "UAH", connection.getDates(), toDynamicCurrency.toUpperCase()));
            service.calculateBasicDynamics();
            return service.predict();
        } else if (algorithm.equals("Non-Linear Regression")) {
            List<Currency> currencies = jsonParsing.parseDiapason(jsonObjects, "UAH", connection.getDates(), toDynamicCurrency.toUpperCase());
            PowerRegression powerRegression = new PowerRegression(toDynamicCurrency, currencies);

            powerRegression.writeXList();
            powerRegression.calculateInXList();
            powerRegression.calculateInY();
            powerRegression.calculateInX2List();
            powerRegression.calculateInY2List();
            powerRegression.calculateInXInYList();

            double bPr = powerRegression.calculateB();
            double aPr = powerRegression.calculateA(bPr);
            double regression = powerRegression.calculatePowerRegressionValue(aPr);

            powerRegression.calculateYMinusAvgY();
            powerRegression.calculateExp(regression, bPr);
            powerRegression.calculateYMinusExp2();
            //powerRegression.yMinusExp2List.forEach(System.out::println);


            //Exponential
            ExponentialRegression exponentialRegression = new ExponentialRegression(toDynamicCurrency, currencies);

            exponentialRegression.calculateX();
            exponentialRegression.calculateInY();
            exponentialRegression.calculateX2();
            exponentialRegression.calculateInY2();
            exponentialRegression.calculateXInYList();


            exponentialRegression.calculateYMinusAvgY();
            double exB = exponentialRegression.calculateEquations();
            double exA = exponentialRegression.calculateA(exB);

            exponentialRegression.calculateExp(exponentialRegression.calculateExpRegression(exA),
                    exponentialRegression.calculateEquations());
            exponentialRegression.calculateYMinusExp2();

            double expDeterminationCoeff = exponentialRegression.determinationCoefficient();

            double prDeterminationCoeff = powerRegression.calculateDeterminationCoefficient();

            if (expDeterminationCoeff > prDeterminationCoeff) {
                return exponentialRegression.predictForNextMonths(exA, exB);
            } else {
                return powerRegression.predictForNextMonths(aPr, bPr);
            }

        }

        throw new WebApplicationException(ErrorMessages.unknownException());
    }

}
