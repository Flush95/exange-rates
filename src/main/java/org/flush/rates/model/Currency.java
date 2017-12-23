package org.flush.rates.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Currency {
    private String baseCurrencyName;
    private String date;
    private String price;
    private String toCurrency;
    private String priceSold;

    public Currency() {
    }

    public Currency(String baseCurrencyName, String date, String toCurrency, String price, String priceSold) {
        this.baseCurrencyName = baseCurrencyName;
        this.date = date;
        this.toCurrency = toCurrency;
        this.price = price;
        this.priceSold = priceSold;
    }

    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public String getPriceSold() {
        return priceSold;
    }

    public void setBaseCurrencyName(String baseCurrencyName) {
        this.baseCurrencyName = baseCurrencyName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public void setPriceSold(String priceSold) {
        this.priceSold = priceSold;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "baseCurrencyName='" + baseCurrencyName + '\'' +
                ", date='" + date + '\'' +
                ", price='" + price + '\'' +
                ", toCurrency='" + toCurrency + '\'' +
                ", priceSold='" + priceSold + '\'' +
                '}';
    }
}
