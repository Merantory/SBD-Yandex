package com.merantory.YandexSBD.dto.courier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.merantory.YandexSBD.models.CourierTypeEnum;
import com.merantory.YandexSBD.util.validators.TimeInterval;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class CreateCourierDto {
    @JsonProperty("courier_type")
    private CourierTypeEnum courierType;

    @NotEmpty(message = "should not be empty")
    @JsonProperty("regions")
    private Set<@Positive(message = "values should be positive") Long> regions;

    @JsonProperty("working_hours")
    private Set<@TimeInterval String> workingHours;
}
