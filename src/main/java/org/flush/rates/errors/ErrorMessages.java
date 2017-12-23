package org.flush.rates.errors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.flush.rates.model.CustomError;

public class ErrorMessages {
    private static CustomError message;

    public static Response baseCurrencyException() {
        message = new CustomError("Base Currency Error", 404);
        return Response.status(Status.NOT_FOUND)
                .entity(message)
                .build();
    }

    public static Response symbolsException() {
        message = new CustomError("Symbols error", 400);
        return Response.status(Status.BAD_REQUEST)
                .entity(message)
                .build();
    }

    public static Response numericException() {
        message = new CustomError("Wrong quantity", 400);
        return Response.status(Status.BAD_REQUEST)
                .entity(message)
                .build();
    }

    public static Response dateException() {
        message = new CustomError("Bad Date", 400);
        return Response.status(Status.BAD_REQUEST)
                .entity(message)
                .build();
    }

    public static Response unknownException() {
        message = new CustomError("Unknown Exception", 400);
        return Response.status(Status.BAD_REQUEST)
                .entity(message)
                .build();
    }

    public static Response rateException() {
        message = new CustomError("Rate Exception", 400);
        return Response.status(Status.BAD_REQUEST)
                .entity(message)
                .build();
    }

}