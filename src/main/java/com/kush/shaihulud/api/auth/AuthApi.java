package com.kush.shaihulud.api.auth;

import com.kush.shaihulud.model.ServiceResponse;
import com.kush.shaihulud.model.dto.ActionDto;
import com.kush.shaihulud.model.dto.CustomPage;
import com.kush.shaihulud.model.request.AuthenticationRequest;
import com.kush.shaihulud.model.request.RefreshTokenRequest;
import com.kush.shaihulud.model.response.AuthenticationResponse;
import com.kush.shaihulud.model.response.common.ApiResponse;
import com.kush.shaihulud.service.AuthService;
import com.kush.shaihulud.service.MessagingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthApi {

    private final AuthenticationManager authenticationManager;


    private final AuthService authService;

    private final MessagingService messagingService;


    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@Valid @RequestBody AuthenticationRequest data, HttpServletRequest request) {


        ServiceResponse<AuthenticationResponse> svcResp = authService.authenticate(new UsernamePasswordAuthenticationToken
                    (data.getUsername(), data.getPassword()));
        ApiResponse<AuthenticationResponse> apiResponse = ApiResponse.<AuthenticationResponse>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());


    }

    @PostMapping("/signin/refresh-token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> signInFromRefreshToken(@Valid @RequestBody RefreshTokenRequest data, HttpServletRequest request) {


        ServiceResponse<AuthenticationResponse> svcResp = authService.authenticateRefreshToken(data);
        ApiResponse<AuthenticationResponse> apiResponse = ApiResponse.<AuthenticationResponse>builder().status(false).code(svcResp.getStatusCode().name()).build();
        if (svcResp.getData().isPresent()) {
            apiResponse.setData(svcResp.getData().get());
            apiResponse.setStatus(true);
        }
        apiResponse.setMessage(messagingService.getResponseMessage(svcResp, new String[]{}));
        return new ResponseEntity<>(apiResponse, svcResp.getStatusCode().getHttpStatusCode());


    }
}
