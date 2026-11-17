package com.axlero.dashboardbuilder.common.exception;

/** Thrown when a request is valid JSON but breaks a business rule, e.g. an unknown column. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
