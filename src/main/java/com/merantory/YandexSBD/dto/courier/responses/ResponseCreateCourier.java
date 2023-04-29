package com.merantory.YandexSBD.dto.courier.responses;

import com.merantory.YandexSBD.models.Courier;

import java.util.List;

public record ResponseCreateCourier(List<Courier> couriers) {
}
