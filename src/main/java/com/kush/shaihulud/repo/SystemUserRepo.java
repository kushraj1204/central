package com.kush.shaihulud.repo;


import com.kush.shaihulud.model.entity.auth.SystemRole;
import com.kush.shaihulud.model.entity.auth.SystemUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SystemUserRepo extends JpaRepository<SystemUser, String> {
    Optional<SystemUser> findByUserId(String username);

    Page<SystemUser> findByuserNameContainingIgnoreCase(String keyword, Pageable paging);
    Page<SystemUser> findByUserNameContainingIgnoreCaseAndEntityCreFlg(String keyword,String entityCreFlg, Pageable paging);
    @Query("SELECT u FROM SystemUser u WHERE u.entityCreFlg != 'Y' AND u.delFlg='N'")
    Page<SystemUser> findUnverifiedUsers(Pageable paging);

    Optional<SystemUser> findFirstByUserIdOrEmailId(String userId, String emailId);

    Optional<SystemUser> findFirstByUserId(String userId);
}