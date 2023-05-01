package com.merantory.YandexSBD.dto.order.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.merantory.YandexSBD.dto.order.CreateOrderDto;
import jakarta.validation.Valid;

import java.util.List;

public record RequestCreateOrder (@JsonProperty("orders") List<@Valid CreateOrderDto> orders) {

}
