package com.merantory.YandexSBD.dto.order;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
public class CompleteOrderDto {
    @JsonProperty("order_id")
    @Positive(message = "value should be positive")
    private long orderId;
    @JsonProperty("courier_id")
    @Positive(message = "value should be positive")
    private long courierId;
    @JsonProperty("complete_time")
    private Instant completeTime;
}
