package com.axlero.dashboardbuilder.dto.response;

import java.math.BigDecimal;


/** One bar / point / slice. {@code label} is "Total" when the query is not grouped. */
public record MetricPoint(String label, BigDecimal value) {
}
