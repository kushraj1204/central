package com.kush.shaihulud.repo;

import com.kush.shaihulud.model.entity.auth.SystemRole;
import com.kush.shaihulud.model.entity.auth.SystemUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SystemRoleRepo extends JpaRepository<SystemRole, String> {
    Page<SystemRole> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    List<SystemRole> findByIdIn(Set<String> ids);

    List<SystemRole> findByCodeIn(Set<String> roles);

    Optional<SystemRole> findFirstByCode(String code);

    @Query("SELECT r FROM SystemRole r WHERE r.name like :keyword AND r.entityCreFlg = true AND r.delFlg=false")
    Page<SystemRole> findRoles(String keyword, Pageable paging);

    @Query("SELECT r FROM SystemRole r WHERE r.entityCreFlg = false AND r.delFlg=false")
    Page<SystemRole> findUnverifiedRoles(Pageable paging);

    @Query("SELECT r FROM SystemRole r WHERE r.updateObj is not null")
    Page<SystemRole> findUpdateUnverifiedRoles(Pageable paging);

}