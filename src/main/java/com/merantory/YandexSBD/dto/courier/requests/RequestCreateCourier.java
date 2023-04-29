package com.merantory.YandexSBD.dto.courier.requests;

import com.merantory.YandexSBD.dto.courier.CreateCourierDto;
import jakarta.validation.Valid;

import java.util.List;

public record RequestCreateCourier(List<@Valid CreateCourierDto> couriers) {
    public List<@Valid CreateCourierDto> getCouriers() {
        return couriers;
    }
}
