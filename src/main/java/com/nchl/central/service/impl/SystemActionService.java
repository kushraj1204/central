package com.nchl.central.service.impl;

import com.nchl.central.model.ServiceResponse;
import com.nchl.central.model.dto.ActionDto;
import com.nchl.central.model.dto.CustomPage;
import com.nchl.central.model.entity.auth.SystemAction;
import com.nchl.central.model.enums.AppStatusCode;
import com.nchl.central.repo.SystemActionRepo;
import com.nchl.central.service.ISystemActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nchl.central.service.DtoMapperService.dtoMapper;

@Slf4j
@RequiredArgsConstructor
@Service
public class SystemActionService implements ISystemActionService {
    private final SystemActionRepo repo;

    @Override
    public ServiceResponse<List<ActionDto>> list() {
        try {
            List<SystemAction> actions = repo.findAll();
            return ServiceResponse.of(actions.stream().map(dtoMapper::systemActionToActionDto).toList(), AppStatusCode.S20000);
        } catch (Exception e) {
            log.error("Exception in fetching actions, {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }

    @Override
    public ServiceResponse<CustomPage<ActionDto>> pageList(Integer page, Integer size, String keyword, String sortBy, String sortDirection) {
        Sort.Direction direction = (sortDirection.toUpperCase()).equals(Sort.Direction.DESC.name()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable paging = PageRequest.of(page, size, Sort.by(direction, sortBy));
        try {
            CustomPage<SystemAction> actionPage = new CustomPage<>(repo.findByNameContainingIgnoreCase(keyword, paging));
            List<ActionDto> actionDtoList = actionPage.getData().stream().map(dtoMapper::systemActionToActionDto).toList();
            CustomPage<ActionDto> actionDtoPage = new CustomPage<>();
            actionDtoPage.setData(actionDtoList);
            actionDtoPage.mapData(actionPage);
            return ServiceResponse.of(actionDtoPage, AppStatusCode.S20000);
        } catch (Exception e) {
            log.error("Exception in fetching actions, {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }
}
