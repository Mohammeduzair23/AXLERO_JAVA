
package com.axlero.service;

import com.axlero.dto.MetricRequest;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MetricValidationService {

    private static final Set<String> ALLOWED_FIELDS =
            Set.of(
                    "product",
                    "region",
                    "sales",
                    "quantity",
                    "saleDate"
            );

    public void validate(MetricRequest request) {

        if (request == null || request.getAggregation() == null) {
            throw new IllegalArgumentException(
                    "Invalid metric request"
            );
        }

        validateField(request.getMetric());

        if (request.getGroupBy() != null) {
            for (String field : request.getGroupBy()) {
                validateField(field);
            }
        }

        if (request.getFilters() != null) {
            request.getFilters().forEach(filter -> {
                validateField(filter.getField());

                if (filter.getOperator() == null) {
                    throw new IllegalArgumentException(
                            "Filter operator is required"
                    );
                }
            });
        }
    }

    private void validateField(String field) {

        if (field == null ||
                !ALLOWED_FIELDS.contains(field)) {

            throw new IllegalArgumentException(
                    "Invalid field: " + field
            );
        }
    }
}
