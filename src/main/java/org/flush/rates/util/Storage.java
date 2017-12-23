package org.flush.rates.util;

public interface Storage {

    String FIXER_IO_LATEST = "http://api.fixer.io/latest?base=%s";
    String FIXER_IO_LATEST_WITH_SYMBOLS = "http://api.fixer.io/latest?base=%s&symbols=%s";

    String FIXER_IO_HISTORICAL = "http://api.fixer.io/%s?base=%s";
    String FIXER_IO_HISTORICAL_WITH_SYMBOLS = "http://api.fixer.io/%s?base=%s&symbols=%s";

    String NBU_TOKEN = "ad533c412d1b06df8024901590184c17a5eb4927";

    String NBU_LATEST = "http://api.minfin.com.ua/summary/%s";
    String NBU_HISTORICAL = "http://api.minfin.com.ua/summary/%s/%s";

    String CONVERT_ALL_RATES = "https://v3.exchangerate-api.com/bulk/a5aa1baaf75fe9815aff9f44/%s";
    String CONVERT_PAIR_RATES = "https://v3.exchangerate-api.com/pair/a5aa1baaf75fe9815aff9f44/%s/%s";

}
