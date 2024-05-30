package com.kush.shaihulud.service.impl;

import com.kush.shaihulud.model.entity.AuditDetails;
import com.kush.shaihulud.repo.AuditDetailsRepo;
import com.kush.shaihulud.service.IAuditDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuditDetailsService implements IAuditDetailsService {

    private final AuditDetailsRepo repo;

    @Override
    public void addEntry(String eventFlg, String code, String tableKey) {
        AuditDetails auditDetails = AuditDetails.builder().eventFlg(eventFlg).code(code).tableKey(tableKey).build();
        repo.save(auditDetails);

    }

    @Override
    public void addEntry(String eventFlg, String code, String tableKey, String modifiedField) {
        log.info("Modified field is {}", modifiedField);
        AuditDetails auditDetails = AuditDetails.builder().eventFlg(eventFlg).code(code).tableKey(tableKey)
                .modifiedField(modifiedField.substring(0, Math.min(modifiedField.length(), 200)))
                .build();
        repo.save(auditDetails);

    }
}
