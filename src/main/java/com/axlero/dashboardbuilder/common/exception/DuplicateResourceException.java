package com.axlero.dashboardbuilder.common.exception;

/** Thrown when creating a resource that would violate uniqueness, e.g. a duplicate email. */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}