package com.nchl.central.config.security.jwt;

import com.nchl.central.config.security.SecurityUser;
import com.nchl.central.model.enums.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import static java.util.stream.Collectors.joining;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "roles";
    @Value("${jwt_configs.secret}")
    private String jwtSecret;
    @Value("${jwt_configs.access_token_validity_ms}")
    private long accessTokenValidityInMs;

    @Value("${jwt_configs.refresh_token_validity_ms}")
    private long refreshTokenValidityInMs;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        String secret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Authentication authentication, TokenType tokenType, boolean addClaims) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String username = securityUser.getUserId();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Claims claims = Jwts.claims().setSubject(username);
        if (!authorities.isEmpty() && addClaims) {
            claims.put(AUTHORITIES_KEY, authorities.stream().map(GrantedAuthority::getAuthority).collect(joining(",")));
        }
        Date now = new Date();
        long validityInMs = tokenType.equals(TokenType.REFRESH) ? refreshTokenValidityInMs : accessTokenValidityInMs;
        Date validity = new Date(now.getTime() + validityInMs);
        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity).signWith(this.secretKey, SignatureAlgorithm.HS256).compact();
    }

    public Pair<String, String> getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token).getBody();
        return Pair.of(claims.getSubject(), token);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token);
           log.info("expiration date: {}", claims.getBody().getExpiration());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT jwt: {}", e.getMessage());
        }
        return false;
    }
}