package com.merantory.YandexSBD.dto.courier.responses;

import com.merantory.YandexSBD.dto.courier.CourierDto;
import jakarta.validation.Valid;

import java.util.List;

public record ResponseCreateCourier(List<@Valid CourierDto> couriers) {
}
