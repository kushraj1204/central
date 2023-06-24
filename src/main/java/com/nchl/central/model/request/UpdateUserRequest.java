package com.nchl.central.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateUserRequest {

    @NotBlank(message = "should not be empty")
    @NotNull(message = "is required")
    private String empId;

    @NotBlank(message = "should not be empty")
    @NotNull(message = "is required")
    private String userName;

    private String gender;

    private String systemRole;


}
