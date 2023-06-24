package com.nchl.central.service;

import com.nchl.central.model.dto.UserDto;
import com.nchl.central.model.entity.auth.SystemAction;
import com.nchl.central.model.entity.auth.SystemRole;
import com.nchl.central.model.entity.auth.SystemUser;
import com.nchl.central.model.entity.auth.SystemUserTemp;
import com.nchl.central.model.request.CreateRoleRequest;
import com.nchl.central.model.dto.ActionDto;
import com.nchl.central.model.dto.RoleDto;
import com.nchl.central.model.request.CreateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DtoMapperService {
    DtoMapperService dtoMapper =
            Mappers.getMapper(DtoMapperService.class);


    ActionDto systemActionToActionDto(SystemAction systemAction);

    SystemRole createRoleRequestToSystemRole(CreateRoleRequest createRoleRequest);

    RoleDto systemRoleToRoleDto(SystemRole role);



    @Mapping(source = "systemRoles", target = "roles")
    UserDto systemUserToUserDto(SystemUser systemUser);

    @Mapping(target = "systemRoles", ignore = true)
    SystemUser createUserRequestToSystemUser(CreateUserRequest createUserRequest);

    @Mapping(target = "roles", ignore = true)
    UserDto systemUserTempToUserDto(SystemUserTemp systemUserTemp);

    @Mapping(target = "roleId", ignore = true)
    SystemUserTemp systemUserToSystemUserTemp(SystemUser user);
}
