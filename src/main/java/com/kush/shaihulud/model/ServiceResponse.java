package com.kush.shaihulud.model;


import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.kush.shaihulud.model.enums.AppStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public final class ServiceResponse<T> {

    private final Optional<T> data;
    private final AppStatusCode statusCode;
    private final HashMap<String, List<String>> messages;

    private ServiceResponse(T data, AppStatusCode statusCode, HashMap<String, List<String>> messages) {

        Assert.notNull(statusCode, "StatusCode  must not be null");

        this.data = Objects.nonNull(data) ? Optional.of(data) : Optional.empty();
        this.statusCode = statusCode;
        this.messages = Objects.nonNull(messages) ? messages : new HashMap<>();
    }


    public static <T> ServiceResponse<T> of(T first, AppStatusCode second, HashMap<String, List<String>> third) {
        return new ServiceResponse<>(first, second, third);
    }

    public static <T> ServiceResponse<T> of(AppStatusCode second, HashMap<String, List<String>> third) {
        return new ServiceResponse<>(null, second, third);
    }

    public static <T> ServiceResponse<T> of(AppStatusCode second, List<String> third) {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        third.forEach(str -> hashMap.put(str, new ArrayList<>()));
        return new ServiceResponse<>(null, second, hashMap);
    }

    public static <T> ServiceResponse<T> of(T first, AppStatusCode second) {
        return new ServiceResponse<>(first, second, new HashMap<String, List<String>>());
    }

    public static <T> ServiceResponse<T> of(AppStatusCode second) {
        return new ServiceResponse<>(null, second, new HashMap<String, List<String>>());
    }

    public static <T> ServiceResponse<T> of(T first, AppStatusCode second, List<String> third) {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        third.forEach(str -> hashMap.put(str, new ArrayList<>()));
        return new ServiceResponse<>(first, second, hashMap);
    }


    public Optional<T> getData() {
        return data;
    }


    public AppStatusCode getStatusCode() {
        return statusCode;
    }

    public HashMap<String, List<String>> getMessages() {
        return messages;
    }

    public HashMap<String, List<String>> addMessage(String message) {
        messages.put(message, new ArrayList<>());
        return messages;
    }

    public HashMap<String, List<String>> addMessage(String message, List<String> params) {
        messages.put(message, params);
        return messages;
    }

    public static <T> Collector<ServiceResponse<T>, ?, Map<Optional<T>, AppStatusCode>> toMap() {
        return Collectors.toMap(ServiceResponse::getData, ServiceResponse::getStatusCode);
    }

    @Override
    public boolean equals(@Nullable Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof ServiceResponse<?> serviceResponse)) {
            return false;
        }

        if (!ObjectUtils.nullSafeEquals(data, serviceResponse.data)) {
            return false;
        }
        if (!ObjectUtils.nullSafeEquals(statusCode, serviceResponse.statusCode)) {
            return false;
        }

        return ObjectUtils.nullSafeEquals(messages, serviceResponse.messages);
    }

    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(data);
        result = 31 * result + ObjectUtils.nullSafeHashCode(statusCode);
        result = 31 * result + ObjectUtils.nullSafeHashCode(messages);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s->%s->%s", this.data, this.statusCode, this.messages);
    }
}
