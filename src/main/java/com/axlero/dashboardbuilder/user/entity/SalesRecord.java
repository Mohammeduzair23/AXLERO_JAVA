
package com.axlero.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String product;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sales;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDate saleDate;
}
