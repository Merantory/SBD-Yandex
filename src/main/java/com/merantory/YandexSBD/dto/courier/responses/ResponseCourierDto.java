package com.merantory.YandexSBD.dto.courier.responses;

import com.merantory.YandexSBD.dto.courier.CourierDto;

import java.util.List;

public record ResponseCourierDto(List<CourierDto> courierDto, int offset, int limit) {
}
