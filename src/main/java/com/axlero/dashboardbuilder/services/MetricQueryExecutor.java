package com.axlero.dashboardbuilder.services;

import com.axlero.dashboardbuilder.config.CacheConfig;
import com.axlero.dashboardbuilder.dto.request.MetricRequest;
import com.axlero.dashboardbuilder.dto.response.MetricPoint;
import com.axlero.dashboardbuilder.dto.response.MetricResponse;
import com.axlero.dashboardbuilder.entities.SalesRecord;
import com.axlero.dashboardbuilder.enums.AggregationType;
import com.axlero.dashboardbuilder.exceptions.BadRequestException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Builds and runs aggregation queries with the JPA Criteria API.
 * <p>
 * Safety: column names come only from the MetricField / GroupByField enums, values are bound as
 * parameters by Hibernate, and every query is filtered by workspace here, so tenant isolation does
 * not depend on the caller remembering to do it. It does NOT check who is calling: callers must
 * authorise first. That is why the cache sits on this class and not on the service that checks access:
 * a cache hit can never skip the authorisation check.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MetricQueryExecutor {

    /** Upper bound on returned groups, so grouping by a high-cardinality column cannot return a huge payload. */
    static final int MAX_GROUPS = 200;

    private final EntityManager entityManager;

    /** The key contains the workspace id, so one tenant can never be served another tenant's cached numbers. */
    @Cacheable(cacheNames = CacheConfig.METRICS_CACHE, key = "#workspaceId + ':' + #request")
    @Transactional(readOnly = true)
    public MetricResponse execute(UUID workspaceId, MetricRequest request) {
        if (request.dateFrom() != null && request.dateTo() != null && request.dateFrom().isAfter(request.dateTo())) {
            throw new BadRequestException("dateFrom must not be after dateTo");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<SalesRecord> root = query.from(SalesRecord.class);

        Expression<? extends Number> aggregate =
                aggregate(cb, root.<Number>get(request.metricField().attribute()), request.aggregationType());

        List<Predicate> where = new ArrayList<>();
        where.add(cb.equal(root.get("workspace").get("id"), workspaceId));
        if (request.dateFrom() != null) {
            where.add(cb.greaterThanOrEqualTo(root.<LocalDate>get("saleDate"), request.dateFrom()));
        }
        if (request.dateTo() != null) {
            where.add(cb.lessThanOrEqualTo(root.<LocalDate>get("saleDate"), request.dateTo()));
        }
        query.where(where.toArray(new Predicate[0]));

        if (request.groupBy() == null) {
            query.multiselect(aggregate.alias("value"));
        } else {
            Path<Object> group = root.get(request.groupBy().attribute());
            query.multiselect(group.alias("label"), aggregate.alias("value"))
                    .groupBy(group)
                    .orderBy(cb.asc(group));
        }

        List<Tuple> rows = entityManager.createQuery(query).setMaxResults(MAX_GROUPS).getResultList();
        log.debug("Metric query executed: workspace={} metric={} agg={} groupBy={} rows={}",
                workspaceId, request.metricField(), request.aggregationType(), request.groupBy(), rows.size());

        List<MetricPoint> points = rows.stream()
                .map(row -> new MetricPoint(
                        request.groupBy() == null ? "Total" : String.valueOf(row.get("label")),
                        toBigDecimal(row.get("value", Number.class))))
                .toList();
        return new MetricResponse(request.metricField(), request.aggregationType(), request.groupBy(), points);
    }

    private Expression<? extends Number> aggregate(CriteriaBuilder cb, Path<Number> field, AggregationType type) {
        return switch (type) {
            case SUM -> cb.sum(field);
            case COUNT -> cb.count(field);
            case AVG -> cb.avg(field);
            case MIN -> cb.min(field);
            case MAX -> cb.max(field);
        };
    }

    /** An empty result set gives NULL from SUM/AVG/MIN/MAX; the API reports 0 instead. */
    private BigDecimal toBigDecimal(Number value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
    }
}