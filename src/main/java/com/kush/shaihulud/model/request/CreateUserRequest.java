package com.kush.shaihulud.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kush.shaihulud.model.dto.RoleDto;
import com.kush.shaihulud.model.entity.auth.SystemRole;
import com.kush.shaihulud.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateUserRequest {

    @NotBlank(message = "should not be empty")
    @NotNull(message = "is required")
    @Size(max = 50)
    private String empId;

    @NotBlank(message = "should not be empty")
    @NotNull(message = "is required")
    @Size(max = 50)
    private String userId;

    @NotBlank(message = "should not be empty")
    @NotNull(message = "is required")
    @Size(max = 200)
    private String userName;

    @NotBlank(message = "should not be empty")
    @NotNull(message = "is required")
    @Size(min=8,max = 20,message = "Keep your password between 8-20 characters in length")
    private String password;

    @NotBlank(message = "should not be empty")
    @NotNull(message = "is required")
    @Email
    private String emailId;

    private Gender gender;

    private String systemRole;


}
