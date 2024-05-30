package com.kush.shaihulud.service;

import com.kush.shaihulud.model.ServiceResponse;
import com.kush.shaihulud.model.entity.auth.SystemRole;
import com.kush.shaihulud.model.request.CreateRoleRequest;
import com.kush.shaihulud.model.request.UpdateRoleRequest;
import com.kush.shaihulud.model.dto.RoleDto;
import com.kush.shaihulud.model.dto.CustomPage;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ISystemRoleService {
    ServiceResponse<RoleDto> createRole(CreateRoleRequest createRoleRequest);

    SystemRole create(CreateRoleRequest createRoleRequest);

    ServiceResponse<List<RoleDto>> list();

    ServiceResponse<RoleDto> getRole(String id);

    ServiceResponse<RoleDto> update(String id, UpdateRoleRequest updateRoleRequest);


    ServiceResponse<Boolean> roleUpdate(SystemRole role, UpdateRoleRequest updateRoleRequest);

    Optional<SystemRole> getById(String id);

    ServiceResponse<CustomPage<RoleDto>> pageList(Integer page, Integer size, String keyword, String sortBy, String sortDirection);

    List<SystemRole> getRoleByIds(Set<String> roles);

    List<SystemRole> getRoleByCodes(Set<String> roles);

    ServiceResponse<RoleDto> getRoleTemp(String id);

    ServiceResponse<RoleDto> rejecttUpdate(String id);

    ServiceResponse<RoleDto> rejectCreation(String id);

    ServiceResponse<RoleDto> acceptCreation(String id);

    ServiceResponse<CustomPage<RoleDto>> unverifiedRoleUpdatePageList(Integer page, Integer size, String keyword, String sortBy, String sortDirection);

    ServiceResponse<CustomPage<RoleDto>> unverifiedRolePageList(Integer page, Integer size, String keyword, String sortBy, String sortDirection);
}
