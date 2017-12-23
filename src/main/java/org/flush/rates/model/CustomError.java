package org.flush.rates.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CustomError {
    private String errorMessage;
    private int errorCode;

    public CustomError() {

    }
    public CustomError(String errorMessage, int errorCode) {
        super();
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}