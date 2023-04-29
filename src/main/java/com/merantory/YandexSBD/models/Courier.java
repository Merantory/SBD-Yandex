package com.merantory.YandexSBD.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class Courier {
    private long courierId;
    private CourierType courierType;
    private Set<Long> regions;
    private Set<String> workingHours;
    private List<Order> completeOrders;
    private int rating;
    private int earnings;

    public void addRegion(long region) {
        if (regions == null) regions = new HashSet<>();
        regions.add(region);
    }

    public void addWorkingHours(String hours) {
        if (workingHours == null) workingHours = new HashSet<>();
        workingHours.add(hours);
    }
}
