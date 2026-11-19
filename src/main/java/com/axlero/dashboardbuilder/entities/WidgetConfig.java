package com.axlero.dashboardbuilder.entities;

import com.axlero.dashboardbuilder.enums.AggregationType;
import com.axlero.dashboardbuilder.enums.ChartType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "widget_configs")
@Getter
@Setter
@NoArgsConstructor
public class WidgetConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    // Example: amount, quantity, sales_id
    @Column(nullable = false, length = 100)
    private String metricField;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AggregationType aggregationType;

    // Example: region, product, sale_date
    @Column(length = 100)
    private String groupByField;

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