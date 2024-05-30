package com.kush.shaihulud.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomPage<T> implements Serializable {
    private int pageNo;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<T> data;

    public CustomPage(Page<T> data) {
        this.data = data.getContent();
        this.pageNo = data.getPageable().getPageNumber() + 1;
        this.pageSize = data.getPageable().getPageSize();
        this.totalPages = data.getTotalPages();
        this.totalElements = data.getTotalElements();
    }

    public void mapData(CustomPage data) {
        this.pageNo = data.getPageNo();
        this.pageSize = data.getPageSize();
        this.totalPages = data.getTotalPages();
        this.totalElements = data.getTotalElements();
    }


}
