package com.merantory.YandexSBD.dto.courier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.merantory.YandexSBD.models.CourierTypeEnum;
import com.merantory.YandexSBD.util.validators.TimeInterval;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"courier_id", "courier_type", "regions", "working_hours"})
public class CourierDto {
    @JsonProperty("courier_id")
    private long courierId;

    @JsonProperty("courier_type")
    private CourierTypeEnum courierType;

    private Set<@Positive(message = "values should be positive") Long> regions;

    @JsonProperty("working_hours")
    private Set<@TimeInterval String> workingHours;
}
