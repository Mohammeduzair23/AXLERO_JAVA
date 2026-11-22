package com.axlero.dashboardbuilder.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/** A batch is capped so one request cannot hold a transaction open for too long. */
public record SalesBulkRequest(@NotEmpty @Size(max = 1000) List<@Valid SalesRecordRequest> records) {
}
