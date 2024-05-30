package com.kush.shaihulud.service;

import com.kush.shaihulud.model.ServiceResponse;
import com.kush.shaihulud.model.dto.ActionDto;
import com.kush.shaihulud.model.dto.CustomPage;

import java.util.List;

public interface ISystemActionService {
    ServiceResponse<List<ActionDto>> list();

    ServiceResponse<CustomPage<ActionDto>> pageList(Integer page, Integer size, String keyword, String sortBy, String sortDirection);
}
