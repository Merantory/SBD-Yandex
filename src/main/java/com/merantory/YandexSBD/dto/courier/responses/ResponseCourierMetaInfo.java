package com.merantory.YandexSBD.dto.courier.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.merantory.YandexSBD.models.CourierTypeEnum;

import java.util.Set;

@JsonPropertyOrder({"courier_id", "courier_type", "regions", "working_hours", "rating", "earnings"})
public record ResponseCourierMetaInfo(@JsonProperty("courier_id") long courierId,
                                      @JsonProperty("courier_type") CourierTypeEnum courierType,
                                      Set<Long> regions,
                                      @JsonProperty("working_hours") Set<String> workingHours,
                                      int rating,
                                      int earnings) {
}
