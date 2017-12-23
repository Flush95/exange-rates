package org.flush.rates.logic;

import org.flush.rates.model.Currency;
import org.flush.rates.network.Connection;
import org.flush.rates.util.JSONParsing;
import org.json.JSONObject;

import java.util.List;

public class Converter {

   /* public double convertTo(String from, String to, double quantity, String date) {
        double newPrice = 0;
        if (from.equals("UAH")) {
            JSONObject temp = Connection.sendRequestToMinFin(date);
            List<Currency> parsedCurrencies = new JSONParsing().parseFromMinFin(temp, date);
            Currency toObj = null;

            for (Currency currency : parsedCurrencies) {
                if (currency.getToCurrency().equals(to)) {
                    toObj = new Currency(currency.getBaseCurrencyName(), currency.getDate(), currency.getToCurrency(),
                            currency.getPrice(), currency.getPriceSold());
                }
            }
            if (toObj != null)
                newPrice = Double.parseDouble(toObj.getPrice()) * quantity;

        }
        return newPrice;
    }
*/
   public List<Currency> convert(List<Currency> currencyList, String quantity) {
       for (Currency currency : currencyList) {
           if (currency.getBaseCurrencyName().equals("UAH") && currency.getDate().length() <= 10) {
               String baseName = currency.getBaseCurrencyName();
               String toName = currency.getToCurrency();
               currency.setBaseCurrencyName(toName);
               currency.setToCurrency(baseName);
           }
           currency.setPrice(String.valueOf(Double.parseDouble(currency.getPrice()) * Integer.parseInt(quantity)));
       }
       return currencyList;
   }
}
