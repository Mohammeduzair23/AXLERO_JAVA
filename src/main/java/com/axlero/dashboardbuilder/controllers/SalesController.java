package com.axlero.dashboardbuilder.controllers;

import com.axlero.dashboardbuilder.dto.request.SalesBulkRequest;
import com.axlero.dashboardbuilder.dto.response.SalesImportResponse;
import com.axlero.dashboardbuilder.services.SalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalesImportResponse addRecords(@AuthenticationPrincipal UserDetails principal,
                                          @PathVariable UUID workspaceId,
                                          @Valid @RequestBody SalesBulkRequest request) {
        return salesService.addRecords(principal.getUsername(), workspaceId, request);
    }
}