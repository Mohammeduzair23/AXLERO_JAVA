package com.axlero.dashboardbuilder.dto.response;

import com.axlero.dashboardbuilder.enums.AggregationType;
import com.axlero.dashboardbuilder.enums.GroupByField;
import com.axlero.dashboardbuilder.enums.MetricField;

import java.util.List;

public record MetricResponse(MetricField metricField, AggregationType aggregationType,
                             GroupByField groupBy, List<MetricPoint> data
) {
}
