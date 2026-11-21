package com.axlero.dashboardbuilder.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesRecordRequest(
        @NotBlank @Size(max = 50) String region,
        @NotBlank @Size(max = 100) String product,
        @NotNull @PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal amount,
        @NotNull @Positive Integer quantity,
        @NotNull LocalDate saleDate
) {
}