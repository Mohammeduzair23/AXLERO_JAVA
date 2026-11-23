package com.axlero.dashboardbuilder.services;
import java.time.LocalDate;
import com.axlero.dashboardbuilder.dto.request.MetricRequest;
import com.axlero.dashboardbuilder.dto.request.WidgetRequest;
import com.axlero.dashboardbuilder.dto.response.MetricResponse;
import com.axlero.dashboardbuilder.dto.response.WidgetResponse;
import com.axlero.dashboardbuilder.entities.Dashboard;
import com.axlero.dashboardbuilder.entities.WidgetConfig;
import com.axlero.dashboardbuilder.enums.WorkspaceRole;
import com.axlero.dashboardbuilder.exceptions.ResourceNotFoundException;
import com.axlero.dashboardbuilder.repositories.DashboardRepository;
import com.axlero.dashboardbuilder.repositories.WidgetConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Widget definitions inside a dashboard, plus running a widget as a metric query.
 * metricField / groupByField are whitelist enums, so a saved widget can never name an arbitrary column.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WidgetService {

    private final WidgetConfigRepository widgetRepository;
    private final DashboardRepository dashboardRepository;
    private final WorkspaceAccessService access;
    private final MetricQueryExecutor metricQueryExecutor;

    @Transactional
    public WidgetResponse create(String email, UUID workspaceId, UUID dashboardId, WidgetRequest request) {
        access.requireRole(workspaceId, email, WorkspaceRole.EDITOR);
        WidgetConfig widget = new WidgetConfig();
        widget.setDashboard(findDashboard(workspaceId, dashboardId));
        apply(widget, request);
        widgetRepository.save(widget);
        log.info("Widget created: id={} dashboard={}", widget.getId(), dashboardId);
        return toResponse(widget);
    }

    @Transactional(readOnly = true)
    public List<WidgetResponse> list(String email, UUID workspaceId, UUID dashboardId) {
        access.requireRole(workspaceId, email, WorkspaceRole.VIEWER);
        findDashboard(workspaceId, dashboardId);
        return widgetRepository.findByDashboardIdOrderByDisplayOrderAsc(dashboardId).stream()
                .map(this::toResponse)
                .toList();
    }

    /** Runs the widget's saved configuration and returns the numbers to draw. */
    @Transactional(readOnly = true)
    public MetricResponse getData(
            String email,
            UUID workspaceId,
            UUID dashboardId,
            UUID widgetId,
            LocalDate dateFrom,
            LocalDate dateTo) {

        access.requireRole(workspaceId, email, WorkspaceRole.VIEWER);

        findDashboard(workspaceId, dashboardId);

        WidgetConfig widget = findWidget(dashboardId, widgetId);

        return metricQueryExecutor.execute(
                workspaceId,
                new MetricRequest(
                        widget.getMetricField(),
                        widget.getAggregationType(),
                        widget.getGroupByField(),
                        dateFrom,
                        dateTo
                )
        );
    }

    @Transactional
    public WidgetResponse update(String email, UUID workspaceId, UUID dashboardId, UUID widgetId,
                                 WidgetRequest request) {
        access.requireRole(workspaceId, email, WorkspaceRole.EDITOR);
        findDashboard(workspaceId, dashboardId);
        WidgetConfig widget = findWidget(dashboardId, widgetId);
        apply(widget, request);
        log.info("Widget updated: id={}", widgetId);
        return toResponse(widget);
    }

    @Transactional
    public void delete(String email, UUID workspaceId, UUID dashboardId, UUID widgetId) {
        access.requireRole(workspaceId, email, WorkspaceRole.EDITOR);
        findDashboard(workspaceId, dashboardId);
        widgetRepository.delete(findWidget(dashboardId, widgetId));
        log.info("Widget deleted: id={}", widgetId);
    }

    private Dashboard findDashboard(UUID workspaceId, UUID dashboardId) {
        return dashboardRepository.findByIdAndWorkspaceId(dashboardId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard", dashboardId));
    }

    private WidgetConfig findWidget(UUID dashboardId, UUID widgetId) {
        return widgetRepository.findByIdAndDashboardId(widgetId, dashboardId)
                .orElseThrow(() -> new ResourceNotFoundException("Widget", widgetId));
    }

    private void apply(WidgetConfig widget, WidgetRequest r) {
        widget.setTitle(r.title().trim());
        widget.setMetricField(r.metricField());
        widget.setAggregationType(r.aggregationType());
        widget.setGroupByField(r.groupByField());
        widget.setChartType(r.chartType());
        widget.setDisplayOrder(r.displayOrder() == null ? 0 : r.displayOrder());
    }

    private WidgetResponse toResponse(WidgetConfig w) {
        return new WidgetResponse(w.getId(), w.getDashboard().getId(), w.getTitle(), w.getMetricField(),
                w.getAggregationType(), w.getGroupByField(), w.getChartType(), w.getDisplayOrder());
    }
}