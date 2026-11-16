
package com.axlero.dto;

import com.axlero.enums.FilterOperator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterRequest {

    private String field;

    private FilterOperator operator;

    private Object value;
}
