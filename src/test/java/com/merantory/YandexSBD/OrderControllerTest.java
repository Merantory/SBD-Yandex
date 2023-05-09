package com.merantory.YandexSBD;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.merantory.YandexSBD.controllers.CustomGlobalExceptionHandler;
import com.merantory.YandexSBD.controllers.OrderController;
import com.merantory.YandexSBD.dto.order.CompleteOrderDto;
import com.merantory.YandexSBD.dto.order.CreateOrderDto;
import com.merantory.YandexSBD.dto.order.OrderDto;
import com.merantory.YandexSBD.dto.order.requests.RequestCompleteOrderDto;
import com.merantory.YandexSBD.dto.order.requests.RequestCreateOrder;
import com.merantory.YandexSBD.models.Order;
import com.merantory.YandexSBD.services.OrderService;
import com.merantory.YandexSBD.util.exceptions.order.OrderInvalidRequestParamsException;
import com.merantory.YandexSBD.util.exceptions.order.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderController(orderService);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(CustomGlobalExceptionHandler.class)
                .build();
    }

    @Test
    public void getOrdersListTest() throws Exception {
        int defaultOffset = 0;
        int defaultLimit = 1;

        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(orderService, times(1)).getOrdersList(defaultOffset, defaultLimit);
    }
}
