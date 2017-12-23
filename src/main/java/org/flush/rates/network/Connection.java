package org.flush.rates.network;

import org.flush.rates.util.Storage;
import org.flush.rates.util.Util;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class Connection {
    private URL urlObj;
    private HttpURLConnection connection;
    private List<String> dates = new ArrayList<>();

    public Connection(){}

    public JSONObject sendRequestToFixerIO(String date, String currency, String symbols) {
        try {
            if (date == null || date.isEmpty())
                if (symbols == null || symbols.isEmpty()) urlObj = new URL(String.format(Storage.FIXER_IO_LATEST, currency));
                else urlObj = new URL(String.format(Storage.FIXER_IO_LATEST_WITH_SYMBOLS, currency, symbols));
            else
                if (symbols == null || symbols.isEmpty()) urlObj = new URL(String.format(Storage.FIXER_IO_HISTORICAL, date, currency));
                else urlObj = new URL(String.format(Storage.FIXER_IO_HISTORICAL_WITH_SYMBOLS, date, currency, symbols));
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject(getJSONString(connection));
    }

    public JSONObject sendRequestToFreeCurrencyConverter(String startDate, String endDate, String baseCurrency, String toCurrency) {
        try {
            urlObj =  new URL("https://free.currencyconverterapi.com/api/v5/convert?q=" + baseCurrency + "_" + toCurrency +
            "," + toCurrency + "_" + baseCurrency + "&compact=ultra&date=" + startDate + "&endDate=" + endDate);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(getJSONString(connection));
    }

    public JSONObject oneRequestToFreeCurrencyConverter(String date, String baseCurrency, String toCurrency) {
        try {
            urlObj =  new URL("https://free.currencyconverterapi.com/api/v5/convert?q=" + baseCurrency + "_" + toCurrency +
                    "," + toCurrency + "_" + baseCurrency + "&compact=ultra&date=" + date);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(getJSONString(connection));
    }

    public JSONObject sendRequestToMinFin(String date) {
        try {
            if (date == null) urlObj = new URL(String.format(Storage.NBU_LATEST, Storage.NBU_TOKEN));
            else urlObj = new URL(String.format(Storage.NBU_HISTORICAL, Storage.NBU_TOKEN, date));
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(getJSONString(connection));
    }

    public JSONObject sendRequestToConverter(String baseCurrency, String toCurrency) {
        try {
            if (toCurrency == null) urlObj = new URL(String.format(Storage.CONVERT_ALL_RATES, baseCurrency));
            else urlObj = new URL(String.format(Storage.CONVERT_PAIR_RATES, baseCurrency, toCurrency));
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(getJSONString(connection));
    }

    public List<JSONObject> openDiapasonConnection(String startDate, String endDate, String baseCurrency, String symbols) {
        LocalDate startDateObj = Util.getLocalDate(startDate);
        LocalDate endDateObj = Util.getLocalDate(endDate);
        List<JSONObject> jsonObjects = new ArrayList<>();

        for (LocalDate date = startDateObj; date.isBefore(endDateObj.plusDays(1)); date = date.plusDays(1)) {
            if (baseCurrency.equals("UAH")) jsonObjects.add(sendRequestToMinFin(date.toString()));
            else jsonObjects.add(sendRequestToFixerIO(date.toString(), baseCurrency, symbols));
            dates.add(date.toString());
        }
        return jsonObjects;
    }


    public List<JSONObject> predictionsRequests(LocalDate startDate, LocalDate endDate, String baseCurrency, String toCurrency) {
        LocalDate day = endDate;
        LocalDate start = startDate;
        int difference = day.getDayOfMonth() / 8;

        System.out.println(difference);
        System.out.println(endDate);
        List<URL> urls = new ArrayList<>();

        for (int i = 0; i < difference; i++) {
            try {
                start = startDate.plusDays(8);
                urls.add(new URL("https://free.currencyconverterapi.com/api/v5/convert?q=" + baseCurrency + "_" + toCurrency +
                        "," + toCurrency + "_" + baseCurrency + "&compact=ultra&date=" + startDate.toString() + "&endDate=" + start.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            startDate = startDate.plusDays(9);
        }

        int secondDifference = endDate.getDayOfMonth() - start.getDayOfMonth() - 1;

        if (secondDifference > 0) {
            try {
                urls.add(new URL("https://free.currencyconverterapi.com/api/v5/convert?q=" + baseCurrency + "_" + toCurrency +
                        "," + toCurrency + "_" + baseCurrency + "&compact=ultra&date=" + endDate.minusDays(secondDifference) + "&endDate=" + endDate.toString()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        List<JSONObject> jsonObjects = new ArrayList<>();
        for (URL url : urls) {
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsonObjects.add(new JSONObject(connection));
        }
        return jsonObjects;
    }

    private String getJSONString(HttpURLConnection connection) {
        StringBuffer responseBuffer = new StringBuffer();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                responseBuffer.append(inputLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBuffer.toString();
    }

    public List<String> getDates() {
        return dates;
    }
}
