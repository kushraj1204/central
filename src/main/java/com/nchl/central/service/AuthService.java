package com.nchl.central.service;

import com.nchl.central.config.security.ContextUser;
import com.nchl.central.config.security.SecurityUser;
import com.nchl.central.config.security.jwt.JwtTokenProvider;
import com.nchl.central.model.ServiceResponse;
import com.nchl.central.model.enums.AppStatusCode;
import com.nchl.central.model.enums.TokenType;
import com.nchl.central.model.request.RefreshTokenRequest;
import com.nchl.central.model.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt_configs.access_token_validity_ms}")
    private long validityInMs;

    @Value("${jwt_configs.refresh_token_validity_ms}")
    private long refreshTokenValidityInMs;

    private final ContextUser contextUser;
    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;
    @Qualifier("userDetailsService")
    private final UserDetailsService userDetailsService;


    public AuthenticationResponse generateTokens(Authentication authentication) {
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.createToken(authentication, TokenType.ACCESS, true);
        String refreshToken = jwtTokenProvider.createToken(authentication, TokenType.REFRESH, false);
        return new AuthenticationResponse(user.getUserId(),
                accessToken, refreshToken, (int) (validityInMs / 1000), (int) (refreshTokenValidityInMs / 1000));
    }

    public ServiceResponse<AuthenticationResponse> authenticate(Authentication auth) {
        try {
            Authentication authentication = authenticationManager.authenticate(auth);
            AuthenticationResponse authenticationResponse = generateTokens(authentication);
            return ServiceResponse.of(authenticationResponse, AppStatusCode.S20006);
        } catch (AuthenticationException e) {
            log.error("{}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E40005);

        }
    }

    public ServiceResponse<AuthenticationResponse> authenticateRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            Pair<String, String> data = jwtTokenProvider.getAuthentication(refreshTokenRequest.getRefreshToken());
            SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(data.getFirst());
            if (user != null) {
                Authentication auth = new UsernamePasswordAuthenticationToken(user, data.getSecond(), user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                AuthenticationResponse authenticationResponse = generateTokens(auth);
                return ServiceResponse.of(authenticationResponse, AppStatusCode.S20006);
            } else {
                log.error("User associated with token not found");
                return ServiceResponse.of(AppStatusCode.E40008);

            }
        } catch (AuthenticationException e) {
            log.error("{}", e.getLocalizedMessage());
            return ServiceResponse.of(AppStatusCode.E40005);

        }
    }


}
