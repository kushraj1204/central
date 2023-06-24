package com.nchl.central.repo;

import com.nchl.central.model.entity.AuditDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditDetailsRepo extends JpaRepository<AuditDetails, String> {
}
