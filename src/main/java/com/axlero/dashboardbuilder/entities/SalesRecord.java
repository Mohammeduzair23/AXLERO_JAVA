package com.axlero.dashboardbuilder.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** One sale. The dataset widgets aggregate over; always owned by exactly one workspace. */
@Entity
@Table(name = "sales_records",
        indexes = @Index(name = "idx_sales_workspace_date", columnList = "workspace_id, sale_date"))
@Getter
@Setter
@NoArgsConstructor
public class SalesRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(nullable = false, length = 50)
    private String region;

    @Column(nullable = false, length = 100)
    private String product;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDate saleDate;
}