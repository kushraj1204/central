package com.nchl.central.api.auth;

import com.nchl.central.model.ServiceResponse;
import com.nchl.central.model.dto.ActionDto;
import com.nchl.central.model.dto.CustomPage;
import com.nchl.central.model.response.common.ApiResponse;
import com.nchl.central.service.MessagingService;
import com.nchl.central.service.impl.SystemActionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/system-action")
@RequiredArgsConstructor
@Slf4j
public class SystemActionApi {

    private final SystemActionService actionService;

    private final MessagingService messagingService;


    @GetMapping("")
    public ResponseEntity<ApiResponse<List<ActionDto>>> list(HttpServletRequest request) {
        ServiceResponse<List<ActionDto>> svcResp=actionService.list();
        ApiResponse<List<ActionDto>> apiResponse = ApiResponse.<List<ActionDto>>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"actions"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());

    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<CustomPage<ActionDto>>> getPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                      @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                                      @RequestParam(value = "sort_by", defaultValue = "id") String sortBy,
                                                                      @RequestParam(value = "sort_direction", defaultValue = "DESC") String sortDirection,
                                                                      HttpServletRequest request) {
        page = (page < 0) ? 0 : page - 1;
        size = (size < 1) ? 1 : size;
        ServiceResponse<CustomPage<ActionDto>> svcResp = actionService.pageList(page, size, keyword, sortBy, sortDirection);
        ApiResponse<CustomPage<ActionDto>> apiResponse = ApiResponse.<CustomPage<ActionDto>>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{"actions"}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());
    }

}
