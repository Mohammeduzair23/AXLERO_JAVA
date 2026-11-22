package com.axlero.dashboardbuilder.dto.response;

import com.axlero.dashboardbuilder.enums.AggregationType;
import com.axlero.dashboardbuilder.enums.ChartType;
import com.axlero.dashboardbuilder.enums.GroupByField;
import com.axlero.dashboardbuilder.enums.MetricField;

import java.util.UUID;

public record WidgetResponse(
        UUID id,
        UUID dashboardId,
        String title,
        MetricField metricField,
        AggregationType aggregationType,
        GroupByField groupByField,
        ChartType chartType,
        Integer displayOrder
) {
}
