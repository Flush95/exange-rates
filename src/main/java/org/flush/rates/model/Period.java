package org.flush.rates.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Period {

    private String period;
    private Double value;

    public Period() {
    }

    public Period(String period, Double value) {
        this.period = period;
        this.value = value;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Period{" +
                "period='" + period + '\'' +
                ", value=" + value +
                '}';
    }
}
