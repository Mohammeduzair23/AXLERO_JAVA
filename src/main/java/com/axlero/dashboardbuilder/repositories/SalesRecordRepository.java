package com.axlero.dashboardbuilder.repositories;

import com.axlero.dashboardbuilder.entities.SalesRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalesRecordRepository extends JpaRepository<SalesRecord, UUID> {
}
