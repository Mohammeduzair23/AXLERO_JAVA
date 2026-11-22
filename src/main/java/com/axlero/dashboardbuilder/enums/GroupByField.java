package com.axlero.dashboardbuilder.enums;

/** SalesRecord columns a widget may group by. */
public enum GroupByField {
    REGION("region"),
    PRODUCT("product"),
    SALE_DATE("saleDate");

    private final String attribute;

    GroupByField(String attribute) {
        this.attribute = attribute;
    }

    /** The JPA attribute name on SalesRecord. */
    public String attribute() {
        return attribute;
    }
}