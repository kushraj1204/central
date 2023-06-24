package com.nchl.central.service;

import com.nchl.central.model.ServiceResponse;
import com.nchl.central.model.dto.ActionDto;
import com.nchl.central.model.dto.CustomPage;

import java.util.List;

public interface ISystemActionService {
    ServiceResponse<List<ActionDto>> list();

    ServiceResponse<CustomPage<ActionDto>> pageList(Integer page, Integer size, String keyword, String sortBy, String sortDirection);
}
