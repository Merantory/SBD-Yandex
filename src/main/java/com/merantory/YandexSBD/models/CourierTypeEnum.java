package com.merantory.YandexSBD.models;

import java.util.Arrays;

public enum CourierTypeEnum {
    FOOT(1),
    BIKE(2),
    AUTO(3);

    private final int value;
    private CourierTypeEnum(int value) {
        this.value = value;
    }

    public static CourierTypeEnum valueOf(int value) {
        return Arrays.stream(values()).filter(type -> type.value == value).findFirst().orElse(null);
    }
}