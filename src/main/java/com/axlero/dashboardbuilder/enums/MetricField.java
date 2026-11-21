package com.axlero.dashboardbuilder.enums;

/** Numeric SalesRecord columns a widget may aggregate. The only way client input reaches a query. */
public enum MetricField {
    AMOUNT("amount"),
    QUANTITY("quantity");

    private final String attribute;

    MetricField(String attribute) {
        this.attribute = attribute;
    }

    /** The JPA attribute name on SalesRecord. */
    public String attribute() {
        return attribute;
    }
}