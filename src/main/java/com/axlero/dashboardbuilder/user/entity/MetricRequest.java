
package com.axlero.dto;

import com.axlero.enums.AggregationType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MetricRequest {

    private String dataset;

    private String metric;

    private AggregationType aggregation;

    private List<String> groupBy;

    private List<FilterRequest> filters;
}
