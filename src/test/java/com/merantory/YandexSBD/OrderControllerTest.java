package com.merantory.YandexSBD;

import com.merantory.YandexSBD.controllers.CustomGlobalExceptionHandler;
import com.merantory.YandexSBD.controllers.OrderController;
import com.merantory.YandexSBD.models.Order;
import com.merantory.YandexSBD.services.OrderService;
import com.merantory.YandexSBD.util.exceptions.order.OrderInvalidRequestParamsException;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
