package com.merantory.YandexSBD.dto.order.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.merantory.YandexSBD.dto.order.CompleteOrderDto;
import jakarta.validation.Valid;

import java.util.List;

public record RequestCompleteOrderDto(@JsonProperty("complete_info") List<@Valid CompleteOrderDto> completeOrderDto) {
}
