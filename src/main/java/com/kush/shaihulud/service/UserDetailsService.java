package com.kush.shaihulud.service;

import com.kush.shaihulud.config.security.SecurityUser;
import com.kush.shaihulud.model.entity.auth.SystemRole;
import com.kush.shaihulud.model.entity.auth.SystemUser;
import com.kush.shaihulud.repo.SystemUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userDetailsService")
@Slf4j
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private SystemUserRepo systemUserRepo;

    @Override
    public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<SystemUser> user = systemUserRepo.findByUserId(username);

        if (user.isPresent()) {
            SystemRole systemRoles =  user.get().getSystemRoles().iterator().hasNext() ? user.get().getSystemRoles().iterator().next() : null;

            return new SecurityUser(user.get().getUserId(),
                    user.get().getUserName(),
                    user.get().getPassword(),
                    user.get().getEnabled() == 1,
                    systemRoles);

        } else {
            throw new UsernameNotFoundException("User: " + username + " not found");
        }
    }

}
