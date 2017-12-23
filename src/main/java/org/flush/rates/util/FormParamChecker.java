package org.flush.rates.util;


import org.flush.rates.errors.ErrorMessages;
import javax.ws.rs.WebApplicationException;

public class FormParamChecker {

    public boolean isRate(String rate) {
        if (Util.rates.contains(rate))
            return true;
        else
            return false;
    }

    public boolean checkSymbols(String symbols) {
        if (symbols == null || symbols.isEmpty() || symbols.length() < 3)
            return false;
        else
            return true;
    }

    public String formatCurrency(String currency) {
        if (currency.length() < 3)
            throw new WebApplicationException(ErrorMessages.baseCurrencyException());
        if (!currency.contains(" "))
            throw new WebApplicationException(ErrorMessages.baseCurrencyException());
        String[] temp = currency.split(" ");
        if (temp[0] != null && !temp[0].isEmpty() && temp[0].length() == 3)
            return temp[0];
        else
            throw new WebApplicationException(ErrorMessages.baseCurrencyException());
    }

    public String formatSymbols(String symbols) {
        String[] temp = symbols.split(", |(,)|( )");
        StringBuilder builder = new StringBuilder();
        for (String symbol : temp) {
            if (!symbol.isEmpty() && Util.rates.contains(symbol))
                builder.append(symbol.toUpperCase()).append(",");
        }
        return builder.toString().substring(0, builder.toString().length() - 1);
    }

    public boolean isNumeric(String s) {
        return s.matches("\\d+");
    }


}
