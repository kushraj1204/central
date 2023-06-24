package com.nchl.central.service.impl;

import com.nchl.central.model.entity.AuditDetails;
import com.nchl.central.repo.AuditDetailsRepo;
import com.nchl.central.service.IAuditDetailsService;
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
