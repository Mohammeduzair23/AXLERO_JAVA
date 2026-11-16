
package com.axlero.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MetricResponse {

    private List<String> labels;

    private List<Object> values;

    private Object total;
}
