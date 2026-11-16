
package com.axlero.service;

import com.axlero.entity.SalesRecord;
import com.axlero.enums.AggregationType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final EntityManager entityManager;

    public List<Tuple> executeSalesQuery(
            AggregationType aggregation) {

        CriteriaBuilder cb =
                entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> query =
                cb.createTupleQuery();

        Root<SalesRecord> root =
                query.from(SalesRecord.class);

        Expression<BigDecimal> salesField =
                root.get("sales");

        Expression<?> aggregate;

        switch (aggregation) {

            case SUM:
                aggregate = cb.sum(salesField);
                break;

            case AVG:
                aggregate = cb.avg(salesField);
                break;

            case MIN:
                aggregate = cb.min(salesField);
                break;

            case MAX:
                aggregate = cb.max(salesField);
                break;

            case COUNT:
                aggregate = cb.count(root);
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported aggregation"
                );
        }

        query.multiselect(
                root.get("region").alias("region"),
                aggregate.alias("result")
        );

        query.groupBy(root.get("region"));

        query.orderBy(
                cb.desc(aggregate)
        );

        return entityManager
                .createQuery(query)
                .getResultList();
    }
}
