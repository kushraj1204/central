package com.kush.shaihulud.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
//@RoleCreationActionConstraint
public class CreateRoleRequest {

    @NotBlank(message = "should not be empty")
    @NotNull(message = "is required")
    private String code;

    @NotBlank(message = "should not be empty")
    @NotNull(message = "is required")
    private String name;

    @NotBlank(message = "should not be empty")
    @NotNull(message = "is required")
    private String actionScope;
    private String description;

    private Set<String> actionCode;


}
