package com.axlero.dashboardbuilder.entities;

import com.axlero.dashboardbuilder.enums.AggregationType;
import com.axlero.dashboardbuilder.enums.ChartType;
import com.axlero.dashboardbuilder.enums.GroupByField;
import com.axlero.dashboardbuilder.enums.MetricField;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "widget_configs", indexes = @Index(name = "idx_widgets_dashboard", columnList = "dashboard_id"))
@Getter
@Setter
@NoArgsConstructor
public class WidgetConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MetricField metricField;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AggregationType aggregationType;

    // null means "no grouping" (a single KPI value)
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private GroupByField groupByField;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChartType chartType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dashboard_id", nullable = false)
    private Dashboard dashboard;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}