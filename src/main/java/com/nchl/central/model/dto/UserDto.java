package com.nchl.central.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nchl.central.model.entity.auth.SystemRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    private Long id;

    private String empId;

    private String userId;

    private String userName;

    private String emailId;

    private String gender;

    private int enabled;

    private int accountNonExpired;

    private int credentialNonExpired;

    private int accountNonLocked;

    private String delFlg;

    private Integer noOfLoginAttempts;

    private LocalDateTime pwdLchgDate;

    private Set<RoleDto> roles;
}
