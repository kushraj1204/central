package com.kush.shaihulud.service.impl;

import com.kush.shaihulud.config.security.ContextUser;
import com.kush.shaihulud.model.ServiceResponse;
import com.kush.shaihulud.model.dto.ChangeSummaryDto;
import com.kush.shaihulud.model.dto.CustomPage;
import com.kush.shaihulud.model.dto.UserDto;
import com.kush.shaihulud.model.entity.auth.SystemRole;
import com.kush.shaihulud.model.entity.auth.SystemUser;
import com.kush.shaihulud.model.entity.auth.SystemUserTemp;
import com.kush.shaihulud.model.enums.AppStatusCode;
import com.kush.shaihulud.model.enums.AuditCode;
import com.kush.shaihulud.model.enums.AuditEventFlag;
import com.kush.shaihulud.model.enums.YesNoFlag;
import com.kush.shaihulud.model.request.CreateUserRequest;
import com.kush.shaihulud.model.request.UpdateUserRequest;
import com.kush.shaihulud.repo.SystemUserRepo;
import com.kush.shaihulud.repo.SystemUserTempRepo;
import com.kush.shaihulud.service.ISystemUserService;
import com.kush.shaihulud.utils.ChangeSummaryUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.kush.shaihulud.service.DtoMapperService.dtoMapper;

@RequiredArgsConstructor
@Service
@Slf4j
public class SystemUserService implements ISystemUserService {

    private final SystemUserRepo repo;
    private final SystemUserTempRepo tempRepo;

    private final AuditDetailsService auditService;

    private final SystemRoleService roleService;
    private final PasswordEncoder passwordEncoder;

    private final ContextUser user;


    @Override
    public ServiceResponse<UserDto> createUser(CreateUserRequest createUserRequest) {
        Optional<SystemUser> systemUserOpt = userExistsByUserId(createUserRequest.getUserId());
        if (systemUserOpt.isPresent()) {
            return ServiceResponse.of(AppStatusCode.E40006, Arrays.asList("user.id.taken"));
        }
        try {
            UserDto createdUser = dtoMapper.systemUserToUserDto(create(createUserRequest));
            return ServiceResponse.of(createdUser, AppStatusCode.S20001);

        } catch (Exception e) {
            log.error("Exception in adding user : {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50000);
        }
    }

    @Override
    public SystemUser create(CreateUserRequest createUserRequest) {
        SystemUser user = dtoMapper.createUserRequestToSystemUser(createUserRequest);
        List<SystemRole> roles = roleService.getRoleByCodes(new HashSet<>(Collections.singletonList(createUserRequest.getSystemRole())));
        user.setEnabled(0);
        user.setAccountNonExpired(1);
        user.setAccountNonLocked(1);
        user.setCredentialNonExpired(1);
        user.setEntityCreFlg(YesNoFlag.N.name());
        user.setDelFlg(YesNoFlag.N.name());
        user.setNoOfLoginAttempts(0);
        user.setPwdLchgDate(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setSystemRoles(new HashSet<>(roles));
        repo.save(user);
        auditService.addEntry(AuditEventFlag.A.name(), AuditCode.SYSTEMUSER.name(), String.valueOf(user.getId()));
        return user;
    }

    @Override
    public ServiceResponse<List<UserDto>> list() {
        try {
            List<SystemUser> users = repo.findAll();
            return ServiceResponse.of(users.stream().map(dtoMapper::systemUserToUserDto).toList(), AppStatusCode.S20000);
        } catch (Exception e) {
            log.error("Exception in fetching users, {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }

    public ServiceResponse<UserDto> getUser(String id) {
        try {
            Optional<SystemUser> userOpt = getById(id);
            return userOpt.map(systemUser -> ServiceResponse.of(dtoMapper.systemUserToUserDto(systemUser),
                    AppStatusCode.S20005)).orElseGet(() -> ServiceResponse.of(AppStatusCode.E40004));
        } catch (Exception e) {
            log.error("Exception in fetching user : {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50000);
        }

    }

    @Override
    public ServiceResponse<UserDto> update(String id, UpdateUserRequest updateUserRequest) {
        Optional<SystemUser> userOpt = getById(id);
        if (userOpt.isPresent()) {
            SystemUser user = userOpt.get();
            Optional<SystemUserTemp> userTempOpt = getTempUserById(id);
            if(userTempOpt.isEmpty()){
                ServiceResponse<Boolean> userUpdate = initiateUpdateUser(user, updateUserRequest);
                if (userUpdate.getData().isPresent()) {
                    return ServiceResponse.of(dtoMapper.systemUserToUserDto(user), userUpdate.getStatusCode());
                } else {
                    return ServiceResponse.of(userUpdate.getStatusCode(), userUpdate.getMessages());
                }
            }
            else{
                return ServiceResponse.of(AppStatusCode.E40006,Arrays.asList("edit.instance.exists"));
            }
        } else {
            return ServiceResponse.of(AppStatusCode.E40004);
        }
    }


    @Override
    @Transactional
    public ServiceResponse<Boolean> initiateUpdateUser(SystemUser user, UpdateUserRequest updateUserRequest) {
        try {
            ChangeSummaryDto userDifference = findUserDifference(user, updateUserRequest);
            List<String> savedRoles = user.getSystemRoles().stream().map(SystemRole::getCode).toList();
            List<String> updatedRoles = Arrays.asList(updateUserRequest.getSystemRole());
            boolean sameRoles = new HashSet<>(savedRoles).containsAll(updatedRoles) && new HashSet<>(updatedRoles).containsAll(savedRoles);
            List<SystemRole> roles = user.getSystemRoles().stream().toList();
            if (!sameRoles) {
                roles = roleService.getRoleByCodes(new HashSet<>(updatedRoles));
                if (roles.size() != updatedRoles.size()) {
                    log.error("Invalid role code sent");
                    return ServiceResponse.of(AppStatusCode.E40002, List.of("roles"));
                }
            }
            if (!userDifference.isHasChanges()) {
                log.error("No changes found for update");
                return ServiceResponse.of(AppStatusCode.E40001);
            } else {
                SystemUserTemp userTemp = dtoMapper.systemUserToSystemUserTemp(user);
                userTemp.setRoleId(roles.stream().findFirst().orElseGet(SystemRole::new).getCode());
                mapUserChangesForUpdate(userTemp, updateUserRequest);
                tempRepo.save(userTemp);
                auditService.addEntry(AuditEventFlag.E.name(), AuditCode.SYSTEMUSER.name(), String.valueOf(user.getId()),
                        ChangeSummaryUtils.getChangeJson(userDifference));
                log.info("Updated User Successfully");
                return ServiceResponse.of(true, AppStatusCode.S20002);
            }
        } catch (Exception e) {
            log.error("Exception in updating user : {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50000);
        }
    }

    @Override
    public Optional<SystemUser> getById(String id) {
        return repo.findById(id);
    }

    @Override
    public Optional<SystemUserTemp> getTempUserById(String id) {
        return tempRepo.findById(id);
    }

    private void mapUserChangesForUpdate(SystemUserTemp user, UpdateUserRequest updateUserRequest) {
        user.setEmpId(updateUserRequest.getEmpId());
        user.setUserName(updateUserRequest.getUserName());
        user.setGender(updateUserRequest.getGender());
    }

    private ChangeSummaryDto findUserDifference(SystemUser user, UpdateUserRequest updateUserRequest) {
        ChangeSummaryDto userDifference = ChangeSummaryUtils.getChangeSummary(user, updateUserRequest,
                ChangeSummaryUtils.getFieldNames(UpdateUserRequest.class, SystemUser.class));
        List<String> savedRoles = user.getSystemRoles().stream().map(SystemRole::getCode).toList();
        List<String> updatedRoles = Collections.singletonList(updateUserRequest.getSystemRole());
        boolean sameRoles = new HashSet<>(savedRoles).containsAll(updatedRoles) && new HashSet<>(updatedRoles).containsAll(savedRoles);
        if (!sameRoles) {
            userDifference.setHasChanges(true);
            userDifference.getFieldChanges().put("systemRoles",
                    new ChangeSummaryDto.FieldChange(savedRoles.toString(), updatedRoles.toString()));
        }
        return userDifference;
    }


    @Override
    public ServiceResponse<CustomPage<UserDto>> pageList(Integer page, Integer size, String keyword,
                                                         String sortBy, String sortDirection) {
        Sort.Direction direction = (sortDirection.toUpperCase()).equals(Sort.Direction.DESC.name()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable paging = PageRequest.of(page, size, Sort.by(direction, sortBy));
        try {
            CustomPage<SystemUser> userPage = new CustomPage<>(repo.findByUserNameContainingIgnoreCaseAndEntityCreFlg(keyword, YesNoFlag.Y.name(), paging));
            List<UserDto> userDtoList = userPage.getData().stream().map(dtoMapper::systemUserToUserDto).toList();
            CustomPage<UserDto> userDtoPage = new CustomPage<>();
            userDtoPage.setData(userDtoList);
            userDtoPage.mapData(userPage);
            return ServiceResponse.of(userDtoPage, AppStatusCode.S20000);
        } catch (Exception e) {
            log.error("Exception in fetching users {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }

    @Override
    public ServiceResponse<CustomPage<UserDto>> unverifiedUserPageList(Integer page, Integer size, String keyword,
                                                                       String sortBy, String sortDirection) {
        Sort.Direction direction = (sortDirection.toUpperCase()).equals(Sort.Direction.DESC.name()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable paging = PageRequest.of(page, size, Sort.by(direction, sortBy));
        try {
            CustomPage<SystemUser> userPage = new CustomPage<>(repo.findUnverifiedUsers(paging));
            List<UserDto> userDtoList = userPage.getData().stream().map(dtoMapper::systemUserToUserDto).toList();
            CustomPage<UserDto> userDtoPage = new CustomPage<>();
            userDtoPage.setData(userDtoList);
            userDtoPage.mapData(userPage);
            return ServiceResponse.of(userDtoPage, AppStatusCode.S20000);
        } catch (Exception e) {
            log.error("Exception in fetching users {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }

    @Override
    public Optional<SystemUser> userExistsByUserId(String userId) {
        Optional<SystemUser> systemUser = repo.findFirstByUserId(userId);
        return systemUser;
    }

    @Override
    public ServiceResponse<CustomPage<UserDto>> unverifiedUserUpdatePageList(Integer page, Integer size, String keyword,
                                                                             String sortBy, String sortDirection) {
        Sort.Direction direction = (sortDirection.toUpperCase()).equals(Sort.Direction.DESC.name()) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable paging = PageRequest.of(page, size, Sort.by(direction, sortBy));
        try {
            CustomPage<SystemUserTemp> userPage = new CustomPage<>(tempRepo.findUnverifiedUsers(paging));
            List<UserDto> userDtoList = userPage.getData().stream().map(dtoMapper::systemUserTempToUserDto).toList();
            CustomPage<UserDto> userDtoPage = new CustomPage<>();
            userDtoPage.setData(userDtoList);
            userDtoPage.mapData(userPage);
            return ServiceResponse.of(userDtoPage, AppStatusCode.S20000);
        } catch (Exception e) {
            log.error("Exception in fetching users {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50002);
        }
    }
    @Override
    public ServiceResponse<UserDto> acceptCreation(String id){
        Optional<SystemUser> userOpt = getById(id);
        if (userOpt.isPresent()) {
            SystemUser user = userOpt.get();
            user.setEnabled(1);
            user.setEntityCreFlg(YesNoFlag.Y.name());
            user.setEntityCreUserId(this.user.getUser().getUserId());
            user.setEntityCreTime(LocalDateTime.now());
            repo.save(user);
            return ServiceResponse.of(dtoMapper.systemUserToUserDto(user),AppStatusCode.S20002,Arrays.asList("account.verified"));
        } else {
            return ServiceResponse.of(AppStatusCode.E40004);
        }
    }

    @Override
    public ServiceResponse<UserDto> rejectCreation(String id){
        Optional<SystemUser> userOpt = getById(id);
        if (userOpt.isPresent()) {
            SystemUser user = userOpt.get();
            user.setEnabled(0);
            user.setDelFlg(YesNoFlag.Y.name());
            repo.save(user);
            return ServiceResponse.of(dtoMapper.systemUserToUserDto(user),AppStatusCode.S20002,Arrays.asList("account.rejected"));
        } else {
            return ServiceResponse.of(AppStatusCode.E40004);
        }
    }

    @Override
    public ServiceResponse<UserDto> acceptUpdate(String id){
        Optional<SystemUserTemp> userTempOpt = getTempUserById(id);
        Optional<SystemUser> userOpt = getById(id);
        if (userTempOpt.isPresent() && userOpt.isPresent()) {
            SystemUser user = userOpt.get();
            SystemUserTemp userTemp = userTempOpt.get();
            user.setEmpId(userTemp.getEmpId());
            user.setGender(userTemp.getGender());
            user.setUserName(userTemp.getUserName());
            List<SystemRole> roles=roleService.getRoleByCodes(new HashSet<>(Collections.singleton(userTemp.getRoleId())));
            user.setSystemRoles(new HashSet<>(roles));
            repo.save(user);
            tempRepo.delete(userTemp);
            return ServiceResponse.of(dtoMapper.systemUserToUserDto(user),AppStatusCode.S20002,Arrays.asList("account.update.accepted"));
        } else {
            return ServiceResponse.of(AppStatusCode.E40004);
        }
    }

    @Override
    public ServiceResponse<UserDto> rejecttUpdate(String id){
        Optional<SystemUserTemp> userTempOpt = getTempUserById(id);
        if (userTempOpt.isPresent()) {
            SystemUserTemp userTemp = userTempOpt.get();
            tempRepo.delete(userTemp);
            return ServiceResponse.of(dtoMapper.systemUserTempToUserDto(userTemp),AppStatusCode.S20002,Arrays.asList("account.rejected"));
        } else {
            return ServiceResponse.of(AppStatusCode.E40004);
        }
    }


    @Override
    public ServiceResponse<UserDto> getUserTemp(String id) {
        try {
            Optional<SystemUserTemp> userOpt = getTempUserById(id);
            if(userOpt.isPresent()) {
                UserDto userDto = dtoMapper.systemUserTempToUserDto(userOpt.get());
                List<SystemRole> roles=roleService.getRoleByCodes(new HashSet<>(Collections.singleton(userOpt.get().getRoleId())));
                userDto.setRoles(new HashSet<>(roles.stream().map(dtoMapper::systemRoleToRoleDto).toList()));
                return ServiceResponse.of(userDto,
                        AppStatusCode.S20005);
            }
            else{
                return ServiceResponse.of(AppStatusCode.E40004);
            }
        } catch (Exception e) {
            log.error("Exception in fetching user : {}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E50000);
        }
    }
}
