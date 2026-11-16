package com.axlero.dashboardbuilder.metric.dto;

import com.axlero.dashboardbuilder.metric.entity.AggregationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MetricRequest {

    @NotBlank
    private String dataset;

    @NotBlank
    private String column;

    @NotNull
    private AggregationType aggregation;

    private String groupBy;

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public AggregationType getAggregation() {
        return aggregation;
    }

    public void setAggregation(AggregationType aggregation) {
        this.aggregation = aggregation;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }
}
