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

    @Test
    public void invalidPathParamsGetOrdersListTest() throws Exception {
        // Not valid params
        String offset = "-10";
        String limit = "0";

        mockMvc.perform(get("/orders")
                        .param("offset", offset)
                        .param("limit", limit))
                .andExpect(status().isBadRequest())
                // Expect that controller will throw OrderInvalidRequestParamsException
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof OrderInvalidRequestParamsException));

        // getOrderList shouldn't be called
        verify(orderService, times(0)).getOrdersList(anyInt(), anyInt());
    }

    @Test
    public void getOrderTest() throws Exception {
        // Create Order object for service return
        Order order = new Order();
        order.setId(1L);

        // Mock getOrder call in orderService
        when(orderService.getOrder(anyLong())).thenReturn(order);

        // Execute GET-request for following URL "/orders/{order_id}"
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.order_id").value(1L));

        // Check, that method getOrder was called once with expected orderId
        verify(orderService, times(1)).getOrder(1L);
    }

    @Test
    public void getNotExistOrderTest() throws Exception {
        when(orderService.getOrder(anyLong())).thenThrow(OrderNotFoundException.class);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof OrderNotFoundException));

        verify(orderService, times(1)).getOrder(1L);
    }

    @Test
    public void saveOrderTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setWeight(10);
        createOrderDto.setRegions(1L);
        createOrderDto.setDeliveryHours(List.of("10:00-12:00"));
        createOrderDto.setCost(100);
        RequestCreateOrder requestCreateOrder = new RequestCreateOrder(List.of(createOrderDto));

        doNothing().when(orderService).save(anyList());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateOrder))) // Write requestCreateOrder as JSON
                .andExpect(status().isCreated());

        verify(orderService, times(1)).save(anyList());
    }

    @Test
    public void saveInvalidOrderTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setWeight(-10); // Invalid value for save
        createOrderDto.setRegions(1L);
        createOrderDto.setDeliveryHours(List.of("10:00-12:00"));
        createOrderDto.setCost(100);
        RequestCreateOrder requestCreateOrder = new RequestCreateOrder(List.of(createOrderDto));

        doNothing().when(orderService).save(anyList());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateOrder))) // Write requestCreateOrder as JSON
                .andExpect(status().isBadRequest());

        verify(orderService, times(0)).save(anyList());
    }

    @Test
    public void markOrderAsCompletedTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        CompleteOrderDto completeOrderDto = new CompleteOrderDto();
        completeOrderDto.setOrderId(1L);
        completeOrderDto.setCourierId(1L);
        Instant instant = Instant.now();
        completeOrderDto.setCompleteTime(instant);

        Order returnCompletedOrder = new Order();
        returnCompletedOrder.setId(1L);
        returnCompletedOrder.setDeliveryCourierId(1L);
        returnCompletedOrder.setCompletedTime(instant);

        List<Order> orderList = new ArrayList<>();
        orderList.add(returnCompletedOrder);

        RequestCompleteOrderDto requestCompleteOrderDto = new RequestCompleteOrderDto(List.of(completeOrderDto));

        OrderDto resultOrder = new OrderDto();
        resultOrder.setId(1L);
        resultOrder.setCompletedTime(instant);

        doReturn(orderList).when(orderService).markAsCompleted(anyList());

        mockMvc.perform(post("/orders/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCompleteOrderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..order_id").value((int) resultOrder.getId()))
                .andExpect(jsonPath("$..completed_time").isArray());

        verify(orderService, times(1)).markAsCompleted(anyList());
    }
}
