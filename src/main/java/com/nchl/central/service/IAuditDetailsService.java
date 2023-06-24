package com.nchl.central.service;

public interface IAuditDetailsService {
    void addEntry(String eventFlg, String code, String tableKey);

    void addEntry(String eventFlg, String code, String tableKey, String modifiedField);
}
