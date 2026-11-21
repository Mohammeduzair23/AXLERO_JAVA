package com.axlero.dashboardbuilder.dto.request;

import com.axlero.dashboardbuilder.enums.AggregationType;
import com.axlero.dashboardbuilder.enums.ChartType;
import com.axlero.dashboardbuilder.enums.GroupByField;
import com.axlero.dashboardbuilder.enums.MetricField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record WidgetRequest(
        @NotBlank @Size(max = 100) String title,
        @NotNull MetricField metricField,
        @NotNull AggregationType aggregationType,
        GroupByField groupByField,
        @NotNull ChartType chartType,
        @PositiveOrZero Integer displayOrder
) {
}
