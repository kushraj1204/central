package com.nchl.central.service;

import com.nchl.central.model.ServiceResponse;
import com.nchl.central.model.dto.CustomPage;
import com.nchl.central.model.dto.UserDto;
import com.nchl.central.model.entity.auth.SystemUser;
import com.nchl.central.model.entity.auth.SystemUserTemp;
import com.nchl.central.model.request.CreateUserRequest;
import com.nchl.central.model.request.UpdateUserRequest;

import java.util.List;
import java.util.Optional;

public interface ISystemUserService {
    ServiceResponse<UserDto> createUser(CreateUserRequest createUserRequest);

    SystemUser create(CreateUserRequest createUserRequest);

    ServiceResponse<List<UserDto>> list();

    ServiceResponse<UserDto> getUser(String id);

    ServiceResponse<UserDto> update(String id, UpdateUserRequest updateUserRequest);

    ServiceResponse<Boolean> initiateUpdateUser(SystemUser user, UpdateUserRequest updateUserRequest);

    Optional<SystemUser> getById(String id);

    Optional<SystemUserTemp> getTempUserById(String id);

    ServiceResponse<CustomPage<UserDto>> pageList(Integer page, Integer size, String keyword, String sortBy, String sortDirection);

    ServiceResponse<CustomPage<UserDto>> unverifiedUserPageList(Integer page, Integer size, String keyword, String sortBy, String sortDirection);


    Optional<SystemUser> userExistsByUserId(String userId);

    ServiceResponse<CustomPage<UserDto>> unverifiedUserUpdatePageList(Integer page, Integer size, String keyword,
                                                                      String sortBy, String sortDirection);

    ServiceResponse<UserDto> acceptCreation(String id);

    ServiceResponse<UserDto> rejectCreation(String id);

    ServiceResponse<UserDto> acceptUpdate(String id);

    ServiceResponse<UserDto> rejecttUpdate(String id);

    ServiceResponse<UserDto> getUserTemp(String id);
}
