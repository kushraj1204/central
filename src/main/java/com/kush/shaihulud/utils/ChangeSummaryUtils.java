package com.kush.shaihulud.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.kush.shaihulud.model.dto.ChangeSummaryDto;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class ChangeSummaryUtils {

    public static ChangeSummaryDto getChangeSummary(Object originalObject, Object changeDto, List<String> fieldNames) {
        Map<String, ChangeSummaryDto.FieldChange> fieldChanges = new HashMap<>();
        boolean hasChanges = false;

        for (String fieldName : fieldNames) {
            Object originalValue = getValueFromField(originalObject, fieldName);
            Object changedValue = getValueFromField(changeDto, fieldName);

            if (originalValue != null && changedValue != null && !originalValue.equals(changedValue)) {
                ChangeSummaryDto.FieldChange fieldChange = new ChangeSummaryDto.FieldChange();
                fieldChange.setS1(String.valueOf(originalValue));
                fieldChange.setS2(String.valueOf(changedValue));
                fieldChanges.put(fieldName, fieldChange);
                hasChanges = true;
            }
        }

        ChangeSummaryDto changeSummaryDto = new ChangeSummaryDto();
        changeSummaryDto.setFieldChanges(fieldChanges);
        changeSummaryDto.setHasChanges(hasChanges);

        return changeSummaryDto;
    }


    private static Object getValueFromField(Object object, String fieldName) {
        try {
            Class<?> clazz = object.getClass();
            System.out.println(clazz);
            java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Exception: {}", e.getLocalizedMessage());
            return null;
        }
    }

    public static List<String> getFieldNames(Class<?> clazz) {
        List<String> fieldNames = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.")) {
                continue; // Skip object reference fields
            }
            fieldNames.add(field.getName());
        }

        return fieldNames;
    }

    private static boolean isValidField(Field field) {
        Class<?> fieldType = field.getType();
        if (Collection.class.isAssignableFrom(fieldType)) {
            Class<?> componentType = getCollectionComponentType(field);
            return componentType != null && !componentType.getName().startsWith("com.nchl") && (componentType.isPrimitive()
                    || field.getType().getName().startsWith("java."));
        } else if (fieldType.isArray()) {
            Class<?> componentType = fieldType.getComponentType();
            return componentType != null && !componentType.getName().startsWith("com.nchl") && (componentType.isPrimitive()
                    || field.getType().getName().startsWith("java."));
        } else {
            if (!field.getName().startsWith("com.nchl") && (fieldType.isPrimitive()
                    || field.getType().getName().startsWith("java."))) {
                return true;
            }
        }
        return false;
    }

    private static Class<?> getCollectionComponentType(Field field) {
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length > 0 && typeArguments[0] instanceof Class<?>) {
                return (Class<?>) typeArguments[0];
            }
        }
        return null;
    }


    public static List<String> getFieldNames(Class<?> clazz, Class<?> otherClazz) {
        List<String> fieldNames = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (!isValidField(field))
                continue;
            try {
                Field otherField = otherClazz.getDeclaredField(field.getName());
                if (!isValidField(otherField))
                    continue;
                fieldNames.add(field.getName());
            } catch (NoSuchFieldException e) {
            }
        }
        return fieldNames;
    }


    public static String getChangeJson(ChangeSummaryDto changeSummaryDto) {
        String jsonString = "";
        try {
            jsonString = StaticUtils.mapper.writeValueAsString(changeSummaryDto.getFieldChanges());
        } catch (JsonProcessingException e) {
            log.error("Issue parsing ChangeSummary to String");
        }
        return jsonString;
    }

}
