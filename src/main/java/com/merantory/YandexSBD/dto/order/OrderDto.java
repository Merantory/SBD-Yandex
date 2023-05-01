package com.merantory.YandexSBD.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.merantory.YandexSBD.util.validators.TimeInterval;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"order_id", "weight", "regions", "delivery_hours", "cost", "completed_time"})
public class OrderDto {
    @JsonProperty("order_id")
    private long id;

    @Positive(message = "value should be positive")
    @NotNull(message = "should not be null")
    private float weight;

    @Positive(message = "value should be positive")
    @NotNull(message = "should not be null")
    private long regions;

    @JsonProperty("delivery_hours")
    @NotEmpty(message = "should not be empty")
    private List<@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]-((?!00:00)[01]?[0-9]|2[0-3]):[0-5][0-9]$",
            message = "invalid format. Use follow format: 00:00-23:59")
            @NotEmpty
            @TimeInterval
            String> deliveryHours;

    @Min(value = 0, message = "should not be more then 0")
    private int cost;

    @JsonProperty("completed_time")
    private Instant completedTime;
}
