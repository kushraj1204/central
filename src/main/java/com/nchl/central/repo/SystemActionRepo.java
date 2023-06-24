package com.nchl.central.repo;

import com.nchl.central.model.entity.auth.SystemAction;
import com.nchl.central.model.entity.auth.SystemRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemActionRepo extends JpaRepository<SystemAction, String> {
    Page<SystemAction> findByNameContainingIgnoreCase(String keyword, Pageable paging);
}
