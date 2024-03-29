package com.merantory.YandexSBD.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
public class Order {
    private long id;
    private float weight;
    private long regions;
    private String deliveryHours;
    private int cost;
    private Instant completedTime;
    private long deliveryCourierId;
}
