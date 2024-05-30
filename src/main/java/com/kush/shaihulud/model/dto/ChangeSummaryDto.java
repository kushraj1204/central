package com.kush.shaihulud.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class ChangeSummaryDto {
    private Map<String, FieldChange> fieldChanges;
    private boolean hasChanges;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldChange {
        private String s1;
        private String s2;

    }

}


