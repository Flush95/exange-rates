package org.flush.rates.util;

import org.flush.rates.model.Currency;
import org.flush.rates.network.Connection;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.*;

public class JSONParsing {
    //helper. UNUSED
    public static JSONObject parseMinFin(String json) {
        return new JSONObject(json.substring(1, json.length() - 1));
    }

    // parseFromFixerIO is created for parse JSON Object from fixerio
    public List<Currency> parseFromFixerIO(JSONObject jsonObject) {
        String date = jsonObject.getString("date");
        String baseCurrency = jsonObject.getString("base");

        String str = jsonObject.get("rates").toString();
        String[] rates = str.substring(1, str.length() - 1).split(",");

        List<Currency> ratesList = new ArrayList<>();

        for (String s : rates) {
            String[] temp = s.split(":");
            ratesList.add(new Currency(baseCurrency, date, temp[0].replaceAll("/\"", "").replaceAll("/\"", ""), temp[1], null));
        }
        return ratesList;
    }

    public List<Currency> parseFromConverterAll(JSONObject jsonObject) {
        String from = jsonObject.getString("from");
        String timestamp = jsonObject.get("timestamp").toString();
        String str = jsonObject.get("rates").toString();
        String[] rates = str.substring(1, str.length() - 1).split(",");

        List<Currency> ratesList = new ArrayList<>();
        for (String s : rates) {
            String[] temp = s.split(":");
            ratesList.add(new Currency(from, new Date(Long.parseLong(timestamp) * 1000L).toString(), temp[0].replaceAll("/\"", "").replaceAll("/\"", ""), temp[1], null));
        }
        return ratesList;
    }

    public List<Currency> parseOneConverterRate(JSONObject jsonObject) {
        String from = jsonObject.getString("from");
        String timestamp = jsonObject.get("timestamp").toString();
        String to = jsonObject.getString("to");
        String price = jsonObject.get("rate").toString();

        List<Currency> ratesList = new ArrayList<>();
        ratesList.add(new Currency(from, new Date(Long.parseLong(timestamp) * 1000L).toString(), to, price, null));
        return ratesList;
    }

    public List<Currency> parseManyConverterRates(JSONObject jsonObject, String symbols) {
        List<Currency> oldList = parseFromConverterAll(jsonObject);
        List<Currency> newList = new ArrayList<>();
        String[] symbolsArray = symbols.split(",");
        for (String symbol : symbolsArray) {
            for (Currency currency : oldList) {
                if (currency.getToCurrency().substring(1, currency.getToCurrency().length() - 1).equals(symbol))
                    newList.add(currency);
            }
        }
        return newList;
    }

    // parseFromMinFin is created for parse JSON Object from minfin
    public List<Currency> parseFromMinFin(JSONObject jsonObject, String date) {
        JSONObject USD = jsonObject.getJSONObject("usd");
        JSONObject EUR = jsonObject.getJSONObject("eur");
        JSONObject GBP = jsonObject.getJSONObject("gbp");
        JSONObject RUB = jsonObject.getJSONObject("rub");

        List<Currency> currencies = new ArrayList<>();
        if (date == null)
            date = new Date().toString();
        currencies.add(getObject("EUR", EUR, date));
        currencies.add(getObject("USD", USD, date));
        currencies.add(getObject("GBP", GBP, date));
        currencies.add(getObject("RUB", RUB, date));

        return currencies;
    }

    public List<Currency> parseDiapason(List<JSONObject> jsonObjects, String baseCurrency, List<String> dates, String symbols) {
        List<Currency> currencies = new ArrayList<>();
        if (baseCurrency.equals("UAH"))
            for (int i = 0; i < jsonObjects.size(); i++)
                currencies.addAll(Util.filterResultList(parseFromMinFin(jsonObjects.get(i), dates.get(i)), symbols));
        else
            for (JSONObject jsonObject : jsonObjects)
                currencies.addAll(parseFromFixerIO(jsonObject));

        return currencies;
    }

    public List<Currency> parse(JSONObject jsonObjects, String base, String to) {
        JSONObject fromTo = jsonObjects.getJSONObject(base + "_" + to);
        String[] array = fromTo.toString().substring(1, fromTo.toString().length() - 1).split(",");
        List<Currency> currencies = new ArrayList<>();
        for (String s : array) {
            String[] splited = s.split(":");
            currencies.add(new Currency(base, splited[0].substring(1, splited[0].length() - 1), to, splited[1], ""));
        }

        Collections.sort(currencies, new Comparator<Currency>() {
            public int compare(Currency o1, Currency o2) {
                if (o1.getDate() == null || o2.getDate() == null)
                    return 0;
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        return currencies;
    }


    public List<Currency> parseOne(JSONObject jsonObjects, String base, String to) {
        JSONObject fromTo = jsonObjects.getJSONObject(base + "_" + to);
        String[] array = fromTo.toString().substring(1, fromTo.toString().length() - 1).split(",");
        List<Currency> currencies = new ArrayList<>();
        for (String s : array) {
            String[] splited = s.split(":");
            currencies.add(new Currency(base, splited[0].substring(1, splited[0].length() - 1), to, splited[1], ""));
        }

        return currencies;
    }

    //Refactor for MinFin
    private Currency getObject(String to, JSONObject currency, String date) {
        return new Currency("UAH", date, to, currency.getString("bid"), currency.getString("ask"));
    }
}
