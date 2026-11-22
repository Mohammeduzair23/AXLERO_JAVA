package com.axlero.dashboardbuilder.dto.request;

import com.axlero.dashboardbuilder.enums.AggregationType;
import com.axlero.dashboardbuilder.enums.GroupByField;
import com.axlero.dashboardbuilder.enums.MetricField;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * The JSON payload describing a query. Every field is an enum or a date, so there is no free text
 * that could ever end up inside a query. groupBy == null gives one overall value (a KPI).
 * COUNT counts rows that have a value for metricField (all rows, as these columns are NOT NULL).
 */
public record MetricRequest(
        @NotNull MetricField metricField,
        @NotNull AggregationType aggregationType,
        GroupByField groupBy,
        LocalDate dateFrom,
        LocalDate dateTo
) {
}