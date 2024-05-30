package com.kush.shaihulud.model.response.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApiResponse<T> implements Serializable {
    private String message;
    private boolean status;
    private String code;
    private T data;

    private MessageErrorNode errors;
    @JsonIgnore
    private HttpStatus httpStatusCode;

    public void setSuccessValues(T object, HttpStatus httpStatus, String message) {
        this.setStatus(true);
        this.setData(object);
        this.setMessage(message);
        this.setCode(String.valueOf(httpStatus.value()));
        this.setHttpStatusCode(httpStatus);
    }

    public void setFailureValues( HttpStatus httpStatus, String message) {
        this.setStatus(false);
        this.setMessage(message);
        this.setCode(String.valueOf(httpStatus.value()));
        this.setHttpStatusCode(httpStatus);
    }
    public void setSuccessValues(T object,  String message) {
        this.setStatus(true);
        this.setData(object);
        this.setMessage(message);
    }

    public void setFailureValues( String message) {
        this.setStatus(false);
        this.setMessage(message);
    }

}