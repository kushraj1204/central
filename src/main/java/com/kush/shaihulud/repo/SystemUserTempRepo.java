package com.kush.shaihulud.repo;


import com.kush.shaihulud.model.entity.auth.SystemUser;
import com.kush.shaihulud.model.entity.auth.SystemUserTemp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SystemUserTempRepo extends JpaRepository<SystemUserTemp, String> {
    Optional<SystemUserTemp> findByUserId(String username);

    @Query("SELECT u FROM SystemUserTemp u")
    Page<SystemUserTemp> findUnverifiedUsers(Pageable paging);

}