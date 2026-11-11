package com.axlero.dashboardbuilder.common.exception;

/** Thrown when a requested resource does not exist (or is not visible to the caller). */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object id) {
        super(resourceName + " not found with id: " + id);
    }
}