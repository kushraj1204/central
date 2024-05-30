package com.kush.shaihulud.api.auth;

import com.kush.shaihulud.model.ServiceResponse;
import com.kush.shaihulud.model.dto.CustomPage;
import com.kush.shaihulud.model.dto.RoleDto;
import com.kush.shaihulud.model.request.CreateRoleRequest;
import com.kush.shaihulud.model.request.UpdateRoleRequest;
import com.kush.shaihulud.model.response.common.ApiResponse;
import com.kush.shaihulud.service.MessagingService;
import com.kush.shaihulud.service.impl.SystemRoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system-role")
@RequiredArgsConstructor
@Slf4j
public class SystemRoleApi {

    private final SystemRoleService roleService;
    private final MessagingService messagingService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROL00001')")
    public ResponseEntity<ApiResponse<RoleDto>> get(@PathVariable String id,
                                                    HttpServletRequest request) {
        ServiceResponse<RoleDto> svcResp = roleService.getRole(id);
        ApiResponse<RoleDto> apiResponse = ApiResponse.<RoleDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"role"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ROL00001')")
    public ResponseEntity<ApiResponse<List<RoleDto>>> list(HttpServletRequest request) {
        ServiceResponse<List<RoleDto>> svcResp = roleService.list();
        ApiResponse<List<RoleDto>> apiResponse = ApiResponse.<List<RoleDto>>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"roles"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ROL00003')")
    public ResponseEntity<ApiResponse<RoleDto>> create(@Valid @RequestBody CreateRoleRequest createRoleRequest,
                                                       HttpServletRequest request) {
        ServiceResponse<RoleDto> svcResp = roleService.createRole(createRoleRequest);
        ApiResponse<RoleDto> apiResponse = ApiResponse.<RoleDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"role"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('ROL00005')")
    public ResponseEntity<ApiResponse<RoleDto>> update(@RequestBody @Valid UpdateRoleRequest updateRoleRequest,
                                                       @PathVariable String id,
                                                       HttpServletRequest request) {
        ServiceResponse<RoleDto> svcResp = roleService.update(id, updateRoleRequest);
        ApiResponse<RoleDto> apiResponse = ApiResponse.<RoleDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"role"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());

    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('ROL00001')")
    public ResponseEntity<ApiResponse<CustomPage<RoleDto>>> getPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                    @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                                    @RequestParam(value = "sort_by", defaultValue = "created") String sortBy,
                                                                    @RequestParam(value = "sort_direction", defaultValue = "DESC") String sortDirection,
                                                                    HttpServletRequest request) {
        page = (page < 0) ? 0 : page - 1;
        size = (size < 1) ? 1 : size;
        ServiceResponse<CustomPage<RoleDto>> svcResp = roleService.pageList(page, size, keyword, sortBy, sortDirection);
        ApiResponse<CustomPage<RoleDto>> apiResponse = ApiResponse.<CustomPage<RoleDto>>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"roles"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

    ///
    @GetMapping("/create-pending/page")
    @PreAuthorize("hasAuthority('ROL00002')")
    public ResponseEntity<ApiResponse<CustomPage<RoleDto>>> getCreationPendingRolesPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                                        @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                                                        @RequestParam(value = "sort_by", defaultValue = "created") String sortBy,
                                                                                        @RequestParam(value = "sort_direction", defaultValue = "DESC") String sortDirection,
                                                                                        HttpServletRequest request) {

        page = (page < 0) ? 0 : page - 1;
        size = (size < 1) ? 1 : size;
        ServiceResponse<CustomPage<RoleDto>> svcResp = roleService.unverifiedRolePageList(page, size, keyword, sortBy, sortDirection);
        ApiResponse<CustomPage<RoleDto>> apiResponse = ApiResponse.<CustomPage<RoleDto>>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"roles"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

    @GetMapping("/update-pending/page")
    @PreAuthorize("hasAuthority('ROL00006')")
    public ResponseEntity<ApiResponse<CustomPage<RoleDto>>> getUpdatePendingRolesPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                                      @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                                                      @RequestParam(value = "sort_by", defaultValue = "created") String sortBy,
                                                                                      @RequestParam(value = "sort_direction", defaultValue = "DESC") String sortDirection,
                                                                                      HttpServletRequest request) {

        page = (page < 0) ? 0 : page - 1;
        size = (size < 1) ? 1 : size;
        ServiceResponse<CustomPage<RoleDto>> svcResp = roleService.unverifiedRoleUpdatePageList(page, size, keyword, sortBy, sortDirection);
        ApiResponse<CustomPage<RoleDto>> apiResponse = ApiResponse.<CustomPage<RoleDto>>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"roles"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAuthority('ROL00004')")
    public ResponseEntity<ApiResponse<RoleDto>> acceptCreation(
            @PathVariable String id,
            HttpServletRequest request) {
        ServiceResponse<RoleDto> svcResp = roleService.acceptCreation(id);
        ApiResponse<RoleDto> apiResponse = ApiResponse.<RoleDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"role"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());

    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ROL00004')")
    public ResponseEntity<ApiResponse<RoleDto>> rejectCreation(@PathVariable String id,
                                                               HttpServletRequest request) {
        ServiceResponse<RoleDto> svcResp = roleService.rejectCreation(id);
        ApiResponse<RoleDto> apiResponse = ApiResponse.<RoleDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"role"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());

    }

    @PostMapping("/{id}/accept-update")
    @PreAuthorize("hasAuthority('ROL00007')")
    public ResponseEntity<ApiResponse<RoleDto>> acceptUpdate(
            @PathVariable String id,
            HttpServletRequest request) {
        ServiceResponse<RoleDto> svcResp = roleService.acceptUpdate(id);
        ApiResponse<RoleDto> apiResponse = ApiResponse.<RoleDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"role"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());

    }

    @PostMapping("/{id}/reject-update")
    @PreAuthorize("hasAuthority('ROL00007')")
    public ResponseEntity<ApiResponse<RoleDto>> rejectUpdate(@PathVariable String id,
                                                             HttpServletRequest request) {
        ServiceResponse<RoleDto> svcResp = roleService.rejecttUpdate(id);
        ApiResponse<RoleDto> apiResponse = ApiResponse.<RoleDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"role"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());

    }

    @GetMapping("/edited/{id}")
    @PreAuthorize("hasAuthority('ROL00001')")
    public ResponseEntity<ApiResponse<RoleDto>> getEdited(@PathVariable String id,
                                                          HttpServletRequest request) {
        ServiceResponse<RoleDto> svcResp = roleService.getRoleTemp(id);
        ApiResponse<RoleDto> apiResponse = ApiResponse.<RoleDto>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"role"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }
}
