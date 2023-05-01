package com.merantory.YandexSBD.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.merantory.YandexSBD.util.validators.TimeInterval;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CreateOrderDto {
    @Positive(message = "values should be positive")
    @NotNull(message = "should not be null")
    private float weight;

    @Positive(message = "values should be positive")
    @NotNull(message = "should not be null")
    private long regions;

    @JsonProperty("delivery_hours")
    @NotEmpty(message = "should not be empty")
    private List<@TimeInterval String> deliveryHours;

    @Min(value = 0, message = "should not be more then 0")
    @NotNull(message = "should not be null")
    private int cost;
}