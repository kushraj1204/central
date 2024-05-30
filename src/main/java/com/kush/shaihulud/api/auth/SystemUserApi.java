package com.kush.shaihulud.api.auth;

import com.kush.shaihulud.model.ServiceResponse;
import com.kush.shaihulud.model.dto.CustomPage;
import com.kush.shaihulud.model.dto.UserDto;
import com.kush.shaihulud.model.request.CreateUserRequest;
import com.kush.shaihulud.model.request.UpdateUserRequest;
import com.kush.shaihulud.model.response.common.ApiResponse;
import com.kush.shaihulud.service.MessagingService;
import com.kush.shaihulud.service.impl.SystemUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system-user")
@RequiredArgsConstructor
@Slf4j
public class SystemUserApi {
    private final SystemUserService userService;

    private final MessagingService messagingService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USR00001')")
    public ResponseEntity<ApiResponse<UserDto>> get(@PathVariable String id,
                                                    HttpServletRequest request) {
        ServiceResponse<UserDto> svcResp = userService.getUser(id);
        ApiResponse<UserDto> apiResponse = ApiResponse.<UserDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"user"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('USR00001')")
    public ResponseEntity<ApiResponse<List<UserDto>>> list(HttpServletRequest request) {
        ServiceResponse<List<UserDto>> svcResp  = userService.list();
        ApiResponse<List<UserDto>> apiResponse = ApiResponse.<List<UserDto>>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"users"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('USR00003')")
    public ResponseEntity<ApiResponse<UserDto>> create(@Valid @RequestBody CreateUserRequest createUserRequest,
                                                       HttpServletRequest request) {
        ServiceResponse<UserDto> svcResp = userService.createUser(createUserRequest);
        ApiResponse<UserDto> apiResponse = ApiResponse.<UserDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"user"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('USR00005')")
    public ResponseEntity<ApiResponse<UserDto>> update(@RequestBody @Valid UpdateUserRequest updateUserRequest,
                                                       @PathVariable String id,
                                                       HttpServletRequest request) {
        ServiceResponse<UserDto> svcResp = userService.update(id, updateUserRequest);
        ApiResponse<UserDto> apiResponse = ApiResponse.<UserDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"user"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());

    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('USR00001')")
    public ResponseEntity<ApiResponse<CustomPage<UserDto>>> getPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                    @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                                    @RequestParam(value = "sort_by", defaultValue = "created") String sortBy,
                                                                    @RequestParam(value = "sort_direction", defaultValue = "DESC") String sortDirection,
                                                                    HttpServletRequest request) {

        page = (page < 0) ? 0 : page - 1;
        size = (size < 1) ? 1 : size;
        ServiceResponse<CustomPage<UserDto>> svcResp = userService.pageList(page, size, keyword, sortBy, sortDirection);
        ApiResponse<CustomPage<UserDto>> apiResponse = ApiResponse.<CustomPage<UserDto>>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"users"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }


    @GetMapping("/create-pending/page")
    @PreAuthorize("hasAuthority('USR00002')")
    public ResponseEntity<ApiResponse<CustomPage<UserDto>>> getCreationPendingUsersPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                    @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                                    @RequestParam(value = "sort_by", defaultValue = "created") String sortBy,
                                                                    @RequestParam(value = "sort_direction", defaultValue = "DESC") String sortDirection,
                                                                    HttpServletRequest request) {

        page = (page < 0) ? 0 : page - 1;
        size = (size < 1) ? 1 : size;
        ServiceResponse<CustomPage<UserDto>> svcResp = userService.unverifiedUserPageList(page, size, keyword, sortBy, sortDirection);
        ApiResponse<CustomPage<UserDto>> apiResponse = ApiResponse.<CustomPage<UserDto>>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"users"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

    @GetMapping("/update-pending/page")
    @PreAuthorize("hasAuthority('USR00006')")
    public ResponseEntity<ApiResponse<CustomPage<UserDto>>> getUpdatePendingUsersPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                                        @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                                                        @RequestParam(value = "sort_by", defaultValue = "created") String sortBy,
                                                                                        @RequestParam(value = "sort_direction", defaultValue = "DESC") String sortDirection,
                                                                                        HttpServletRequest request) {

        page = (page < 0) ? 0 : page - 1;
        size = (size < 1) ? 1 : size;
        ServiceResponse<CustomPage<UserDto>> svcResp = userService.unverifiedUserUpdatePageList(page, size, keyword, sortBy, sortDirection);
        ApiResponse<CustomPage<UserDto>> apiResponse = ApiResponse.<CustomPage<UserDto>>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"users"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAuthority('USR00004')")
    public ResponseEntity<ApiResponse<UserDto>> acceptCreation(
                                                       @PathVariable String id,
                                                       HttpServletRequest request) {
        ServiceResponse<UserDto> svcResp = userService.acceptCreation(id);
        ApiResponse<UserDto> apiResponse = ApiResponse.<UserDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"user"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());

    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('USR00004')")
    public ResponseEntity<ApiResponse<UserDto>> rejectCreation(@PathVariable String id,
                                                               HttpServletRequest request) {
        ServiceResponse<UserDto> svcResp = userService.rejectCreation(id);
        ApiResponse<UserDto> apiResponse = ApiResponse.<UserDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"user"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());

    }

    @PostMapping("/{id}/accept-update")
    @PreAuthorize("hasAuthority('USR00007')")
    public ResponseEntity<ApiResponse<UserDto>> acceptUpdate(
            @PathVariable String id,
            HttpServletRequest request) {
        ServiceResponse<UserDto> svcResp = userService.acceptUpdate(id);
        ApiResponse<UserDto> apiResponse = ApiResponse.<UserDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"user"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());

    }

    @PostMapping("/{id}/reject-update")
    @PreAuthorize("hasAuthority('USR00007')")
    public ResponseEntity<ApiResponse<UserDto>> rejectUpdate(@PathVariable String id,
                                                               HttpServletRequest request) {
        ServiceResponse<UserDto> svcResp = userService.rejecttUpdate(id);
        ApiResponse<UserDto> apiResponse = ApiResponse.<UserDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"user"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());

    }

    @GetMapping("/edited/{id}")
    @PreAuthorize("hasAuthority('USR00001')")
    public ResponseEntity<ApiResponse<UserDto>> getEdited(@PathVariable String id,
                                                    HttpServletRequest request) {
        ServiceResponse<UserDto> svcResp = userService.getUserTemp(id);
        ApiResponse<UserDto> apiResponse = ApiResponse.<UserDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"user"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }
}
