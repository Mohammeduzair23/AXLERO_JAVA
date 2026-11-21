package com.axlero.dashboardbuilder.services;

import com.axlero.dashboardbuilder.config.CacheConfig;
import com.axlero.dashboardbuilder.dto.request.SalesBulkRequest;
import com.axlero.dashboardbuilder.dto.request.SalesRecordRequest;
import com.axlero.dashboardbuilder.dto.response.SalesImportResponse;
import com.axlero.dashboardbuilder.entities.SalesRecord;
import com.axlero.dashboardbuilder.entities.Workspace;
import com.axlero.dashboardbuilder.entities.WorkspaceMember;
import com.axlero.dashboardbuilder.enums.WorkspaceRole;
import com.axlero.dashboardbuilder.repositories.SalesRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRecordRepository salesRecordRepository;
    private final WorkspaceAccessService access;

    /**
     * New data makes cached numbers stale. Keys are not searchable by workspace, so the whole (small,
     * short-lived) metrics cache is cleared; imports are rare compared to dashboard reads.
     */
    @CacheEvict(cacheNames = CacheConfig.METRICS_CACHE, allEntries = true)
    @Transactional
    public SalesImportResponse addRecords(String email, UUID workspaceId, SalesBulkRequest request) {
        WorkspaceMember member = access.requireRole(workspaceId, email, WorkspaceRole.EDITOR);
        List<SalesRecord> records = request.records().stream()
                .map(r -> toEntity(member.getWorkspace(), r))
                .toList();
        salesRecordRepository.saveAll(records);
        log.info("Sales records added: workspace={} count={}", workspaceId, records.size());
        return new SalesImportResponse(records.size());
    }

    private SalesRecord toEntity(Workspace workspace, SalesRecordRequest r) {
        SalesRecord record = new SalesRecord();
        record.setWorkspace(workspace);
        record.setRegion(r.region().trim());
        record.setProduct(r.product().trim());
        record.setAmount(r.amount());
        record.setQuantity(r.quantity());
        record.setSaleDate(r.saleDate());
        return record;
    }
}