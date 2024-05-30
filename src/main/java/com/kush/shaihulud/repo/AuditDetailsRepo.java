package com.kush.shaihulud.repo;

import com.kush.shaihulud.model.entity.AuditDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditDetailsRepo extends JpaRepository<AuditDetails, String> {
}
