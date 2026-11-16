package com.axlero.dashboardbuilder.metric;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.axlero.dashboardbuilder.metric.dto.MetricRequest;
import com.axlero.dashboardbuilder.metric.entity.AggregationType;
import org.junit.jupiter.api.Test;

class MetricRequestTest {

    @Test
    void shouldStoreMetricConfiguration() {

        MetricRequest request = new MetricRequest();

        request.setDataset("sales");
        request.setColumn("amount");
        request.setAggregation(AggregationType.SUM);
        request.setGroupBy("region");

        assertEquals("sales", request.getDataset());
        assertEquals("amount", request.getColumn());
        assertEquals(AggregationType.SUM, request.getAggregation());
        assertEquals("region", request.getGroupBy());
    }
}
