package com.nchl.central.config;

import com.nchl.central.config.security.SecurityUser;
import com.nchl.central.model.entity.metainfo.SystemUsername;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class BaseDataConfig {

    @Bean
    public AuditorAware<SystemUsername> auditor() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(SecurityUser.class::cast)
                .map(u -> new SystemUsername(u.getUserId()));
    }



}
