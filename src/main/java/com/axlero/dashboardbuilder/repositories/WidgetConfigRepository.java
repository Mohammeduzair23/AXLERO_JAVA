package com.axlero.dashboardbuilder.repositories;

import com.axlero.dashboardbuilder.entities.WidgetConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WidgetConfigRepository extends JpaRepository<WidgetConfig, UUID> {

    List<WidgetConfig> findByDashboardIdOrderByDisplayOrderAsc(UUID dashboardId);

    Optional<WidgetConfig> findByIdAndDashboardId(UUID id, UUID dashboardId);
}
