
package com.axlero.controller;

import com.axlero.service.MetricService;
import com.axlero.service.MetricValidationService;
import com.axlero.dto.MetricRequest;
import com.axlero.enums.AggregationType;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.Tuple;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricController {

    private final MetricService metricService;

    private final MetricValidationService validationService;

    @PostMapping("/query")
    public ResponseEntity<?> executeQuery(
            @RequestBody MetricRequest request) {

        validationService.validate(request);

        if (!"sales".equals(request.getDataset())) {
            return ResponseEntity.badRequest()
                    .body("Unsupported dataset");
        }

        List<Tuple> result =
                metricService.executeSalesQuery(
                        request.getAggregation()
                );

        return ResponseEntity.ok(
                result.stream()
                        .map(row -> java.util.Map.of(
                                "region", row.get("region"),
                                "result", row.get("result")
                        ))
                        .toList()
        );
    }
}
