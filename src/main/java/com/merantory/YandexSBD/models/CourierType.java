package com.merantory.YandexSBD.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourierType {
    private CourierTypeEnum type;
    private int incomeCoefficient;
    private int rateCoefficient;
    private int maxWeight;
    private int maxOrdersCount;
    private int maxRegionsCount;

    public CourierType(CourierTypeEnum type) {
        this.type = type;
    }

    public CourierType(CourierTypeEnum type, int incomeCoefficient, int rateCoefficient,
                       int maxWeight, int maxOrdersCount, int maxRegionsCount) {
        this.type = type;
        this.incomeCoefficient = incomeCoefficient;
        this.rateCoefficient = rateCoefficient;
        this.maxWeight = maxWeight;
        this.maxOrdersCount = maxOrdersCount;
        this.maxRegionsCount = maxRegionsCount;
    }

    public CourierType(int typeIntValue, int incomeCoefficient, int rateCoefficient,
                       int maxWeight, int maxOrdersCount, int maxRegionsCount) {
        this.type = CourierTypeEnum.valueOf(typeIntValue);
        this.incomeCoefficient = incomeCoefficient;
        this.rateCoefficient = rateCoefficient;
        this.maxWeight = maxWeight;
        this.maxOrdersCount = maxOrdersCount;
        this.maxRegionsCount = maxRegionsCount;
    }

    public CourierType(int typeInValue) {
        this.type = CourierTypeEnum.valueOf(typeInValue);
    }
}
