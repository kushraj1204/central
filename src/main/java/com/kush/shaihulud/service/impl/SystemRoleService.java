package com.kush.shaihulud.service.impl;

import com.kush.shaihulud.config.security.ContextUser;
import com.kush.shaihulud.model.ServiceResponse;
import com.kush.shaihulud.model.dto.ChangeSummaryDto;
import com.kush.shaihulud.model.dto.CustomPage;
import com.kush.shaihulud.model.dto.RoleDto;
import com.kush.shaihulud.model.entity.auth.SystemRole;
import com.kush.shaihulud.model.enums.AppStatusCode;
import com.kush.shaihulud.model.enums.AuditCode;
import com.kush.shaihulud.model.enums.AuditEventFlag;
import com.kush.shaihulud.model.request.CreateRoleRequest;
import com.kush.shaihulud.model.request.UpdateRoleRequest;
import com.kush.shaihulud.repo.SystemRoleRepo;
import com.kush.shaihulud.service.ISystemRoleService;
import com.kush.shaihulud.utils.ChangeSummaryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.kush.shaihulud.service.DtoMapperService.dtoMapper;

@RequiredArgsConstructor
@Service
@Slf4j
public class SystemRoleService implements ISystemRoleService {

    private final SystemRoleRepo repo;

    private final AuditDetailsService auditService;

    private final ContextUser user;

    @Override
    public ServiceResponse<RoleDto> createRole(CreateRoleRequest createRoleRequest) {
        Optional<SystemRole> systemRoleOpt = roleExistsByCode(createRoleRequest.getCode());
        if (systemRoleOpt.isPresent()) {
            StringBuilder str = new StringBuilder("Role Code is already taken. ");
            return ServiceResponse.of(AppStatusCode.E40006, Arrays.asList(str.toString()));
        }
        try {
            RoleDto createdRole = dtoMapper.systemRoleToRoleDto(create(createRoleRequest));
            return ServiceResponse.of(createdRole, AppStatusCode.S20001);

        } catch (Exception e) {
            log.error("Exception in adding role : {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50000);
        }
    }

    private Optional<SystemRole> roleExistsByCode(String code) {
        return repo.findFirstByCode(code);
    }

    @Override
    public SystemRole create(CreateRoleRequest createRoleRequest) {
        SystemRole role = dtoMapper.createRoleRequestToSystemRole(createRoleRequest);
        role.setId(UUID.randomUUID().toString());
        role.setEnabled(false);
        role.setEntityCreFlg(false);
        role.setDelFlg(false);
        repo.save(role);
        auditService.addEntry(AuditEventFlag.A.name(), AuditCode.SYSTEMROLE.name(), role.getId());
        return role;
    }

    @Override
    public ServiceResponse<List<RoleDto>> list() {
        try {
            List<SystemRole> roles = repo.findAll();
            return ServiceResponse.of(roles.stream().map(dtoMapper::systemRoleToRoleDto).toList(), AppStatusCode.S20000);
        } catch (Exception e) {
            log.error("Exception in fetching roles, {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }

    @Override
    public ServiceResponse<RoleDto> getRole(String id) {
        try {
            Optional<SystemRole> roleOpt = getById(id);
            return roleOpt.map(systemRole -> ServiceResponse.of(dtoMapper.systemRoleToRoleDto(systemRole),
                    AppStatusCode.S20005)).orElseGet(() -> ServiceResponse.of(AppStatusCode.E40004));
        } catch (Exception e) {
            log.error("Exception in fetching role : {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50000);
        }

    }

    @Override
    public ServiceResponse<RoleDto> update(String id, UpdateRoleRequest updateRoleRequest) {
        Optional<SystemRole> roleOpt = getById(id);
        if (roleOpt.isPresent()) {
            SystemRole role = roleOpt.get();
            if (Objects.nonNull(role.getUpdateObj())) {
                return ServiceResponse.of(AppStatusCode.E40006, Arrays.asList("edit.instance.exists"));
            }
            ServiceResponse<Boolean> roleUpdate = roleUpdate(role, updateRoleRequest);
            if (roleUpdate.getData().isPresent()) {
                return ServiceResponse.of(dtoMapper.systemRoleToRoleDto(role), roleUpdate.getStatusCode());
            } else {
                return ServiceResponse.of(roleUpdate.getStatusCode(), roleUpdate.getMessages());
            }
        } else {
            return ServiceResponse.of(AppStatusCode.E40004);
        }
    }


    @Override
    public ServiceResponse<Boolean> roleUpdate(SystemRole role, UpdateRoleRequest updateRoleRequest) {
        try {
            ChangeSummaryDto roleDifference = findRoleDifference(role, updateRoleRequest);
            if (!roleDifference.isHasChanges()) {
                return ServiceResponse.of(AppStatusCode.E40001);
            } else {
                role.setUpdateObj(updateRoleRequest);
                repo.save(role);
                auditService.addEntry(AuditEventFlag.E.name(), AuditCode.SYSTEMROLE.name(), role.getId(),
                        ChangeSummaryUtils.getChangeJson(roleDifference));
                return ServiceResponse.of(true, AppStatusCode.S20002);
            }
        } catch (Exception e) {
            log.error("Exception in updating role : {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50000);
        }
    }

    @Override
    public Optional<SystemRole> getById(String id) {
        return repo.findById(id);
    }

    private void mapRoleChangesForUpdate(SystemRole role, UpdateRoleRequest updateRoleRequest) {
        role.setName(updateRoleRequest.getName());
        role.setDescription(updateRoleRequest.getDescription());
        role.setActionCode(updateRoleRequest.getActionCode());
    }

    private ChangeSummaryDto findRoleDifference(SystemRole role, UpdateRoleRequest updateRoleRequest) {
        return ChangeSummaryUtils.getChangeSummary(role, updateRoleRequest,
                ChangeSummaryUtils.getFieldNames(UpdateRoleRequest.class));
    }


    @Override
    public ServiceResponse<CustomPage<RoleDto>> pageList(Integer page, Integer size, String keyword,
                                                         String sortBy, String sortDirection) {
        Sort.Direction direction = (sortDirection.toUpperCase()).equals(Sort.Direction.DESC.name()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable paging = PageRequest.of(page, size, Sort.by(direction, sortBy));
        try {
            CustomPage<SystemRole> rolePage = new CustomPage<>(repo.findRoles("%" + keyword + "%", paging));
            List<RoleDto> roleDtoList = rolePage.getData().stream().map(dtoMapper::systemRoleToRoleDto).toList();
            CustomPage<RoleDto> roleDtoPage = new CustomPage<>();
            roleDtoPage.setData(roleDtoList);
            roleDtoPage.mapData(rolePage);
            return ServiceResponse.of(roleDtoPage, AppStatusCode.S20000);
        } catch (Exception e) {
            log.error("Exception in fetching roles :", e);
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }

    @Override
    public List<SystemRole> getRoleByIds(Set<String> roles) {
        return repo.findByIdIn(roles);
    }

    @Override
    public List<SystemRole> getRoleByCodes(Set<String> roles) {
        return repo.findByCodeIn(roles);
    }


    @Override
    public ServiceResponse<RoleDto> getRoleTemp(String id) {
        try {
            Optional<SystemRole> roleOpt = getById(id);
            if (roleOpt.isPresent()) {
                if (Objects.nonNull(roleOpt.get().getUpdateObj())) {
                    UpdateRoleRequest req = roleOpt.get().getUpdateObj();
                    RoleDto roleDto = dtoMapper.systemRoleToRoleDto(roleOpt.get());
                    roleDto.setName(req.getName());
                    roleDto.setDescription(req.getDescription());
                    roleDto.setActionCode(req.getActionCode());
                    return ServiceResponse.of(roleDto,
                            AppStatusCode.S20005);
                } else {
                    return ServiceResponse.of(AppStatusCode.E40004);
                }
            } else {
                return ServiceResponse.of(AppStatusCode.E40004);
            }
        } catch (Exception e) {
            log.error("Exception in fetching role : {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50000);
        }
    }

    @Override
    public ServiceResponse<RoleDto> rejecttUpdate(String id) {
        try {
            Optional<SystemRole> roleOpt = getById(id);
            if (roleOpt.isPresent()) {
                SystemRole role = roleOpt.get();
                if (Objects.nonNull(role.getUpdateObj())) {
                    role.setUpdateObj(null);
                    repo.save(role);
                    RoleDto roleDto = dtoMapper.systemRoleToRoleDto(role);
                    return ServiceResponse.of(roleDto,
                            AppStatusCode.S20005);
                } else {
                    return ServiceResponse.of(AppStatusCode.E40004);
                }
            } else {
                return ServiceResponse.of(AppStatusCode.E40004);
            }
        } catch (Exception e) {
            log.error("Exception in fetching role : {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50000);
        }
    }

    public ServiceResponse<RoleDto> acceptUpdate(String id) {
        try {
            Optional<SystemRole> roleOpt = getById(id);
            if (roleOpt.isPresent()) {
                SystemRole role = roleOpt.get();
                if (Objects.nonNull(role.getUpdateObj())) {
                    UpdateRoleRequest req = role.getUpdateObj();
                    role.setName(req.getName());
                    role.setDescription(req.getDescription());
                    role.setActionCode(req.getActionCode());
                    role.setUpdateObj(null);
                    repo.save(role);
                    RoleDto roleDto = dtoMapper.systemRoleToRoleDto(role);
                    return ServiceResponse.of(roleDto,
                            AppStatusCode.S20005);
                } else {
                    return ServiceResponse.of(AppStatusCode.E40004);
                }
            } else {
                return ServiceResponse.of(AppStatusCode.E40004);
            }
        } catch (Exception e) {
            log.error("Exception in fetching role : {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50000);
        }
    }

    @Override
    public ServiceResponse<RoleDto> rejectCreation(String id) {
        Optional<SystemRole> roleOpt = getById(id);
        if (roleOpt.isPresent()) {
            SystemRole role = roleOpt.get();
            role.setEnabled(false);
            role.setDelFlg(true);
            repo.save(role);
            return ServiceResponse.of(dtoMapper.systemRoleToRoleDto(role), AppStatusCode.S20002, Arrays.asList("rejected", "contact.administrator"));
        } else {
            return ServiceResponse.of(AppStatusCode.E40004);
        }
    }

    @Override
    public ServiceResponse<RoleDto> acceptCreation(String id) {
        Optional<SystemRole> roleOpt = getById(id);
        if (roleOpt.isPresent()) {
            SystemRole role = roleOpt.get();
            role.setEnabled(true);
            role.setEntityCreFlg(true);
            role.setEntityCreUserId(user.getUser().getUserId());
            role.setEntityCreTime(LocalDateTime.now());
            repo.save(role);
            return ServiceResponse.of(dtoMapper.systemRoleToRoleDto(role), AppStatusCode.S20002, Arrays.asList("account.verified"));
        } else {
            return ServiceResponse.of(AppStatusCode.E40004);
        }
    }

    @Override
    public ServiceResponse<CustomPage<RoleDto>> unverifiedRoleUpdatePageList(Integer page, Integer size, String keyword, String sortBy, String sortDirection) {
        Sort.Direction direction = (sortDirection.toUpperCase()).equals(Sort.Direction.DESC.name()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable paging = PageRequest.of(page, size, Sort.by(direction, sortBy));
        try {
            CustomPage<SystemRole> rolePage = new CustomPage<>(repo.findUpdateUnverifiedRoles(paging));
            List<RoleDto> roleDtoList = rolePage.getData().stream().map(dtoMapper::systemRoleToRoleDto).toList();
            CustomPage<RoleDto> roleDtoPage = new CustomPage<>();
            roleDtoPage.setData(roleDtoList);
            roleDtoPage.mapData(rolePage);
            return ServiceResponse.of(roleDtoPage, AppStatusCode.S20000);
        } catch (Exception e) {
            log.error("Exception in fetching roles {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }

    @Override
    public ServiceResponse<CustomPage<RoleDto>> unverifiedRolePageList(Integer page, Integer size, String keyword, String sortBy, String sortDirection) {
        Sort.Direction direction = (sortDirection.toUpperCase()).equals(Sort.Direction.DESC.name()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable paging = PageRequest.of(page, size, Sort.by(direction, sortBy));
        try {
            CustomPage<SystemRole> rolePage = new CustomPage<>(repo.findUnverifiedRoles(paging));
            List<RoleDto> roleDtoList = rolePage.getData().stream().map(dtoMapper::systemRoleToRoleDto).toList();
            CustomPage<RoleDto> roleDtoPage = new CustomPage<>();
            roleDtoPage.setData(roleDtoList);
            roleDtoPage.mapData(rolePage);
            return ServiceResponse.of(roleDtoPage, AppStatusCode.S20000);
        } catch (Exception e) {
            log.error("Exception in fetching roles {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }

    private RoleDto getUpdatedRoleFromRole(SystemRole role) {
        UpdateRoleRequest req = role.getUpdateObj();
        RoleDto roleDto = dtoMapper.systemRoleToRoleDto(role);
        roleDto.setName(req.getName());
        roleDto.setDescription(req.getDescription());
        roleDto.setActionCode(req.getActionCode());
        return roleDto;
    }
}
