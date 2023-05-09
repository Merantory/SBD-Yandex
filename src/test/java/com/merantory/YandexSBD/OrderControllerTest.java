package com.merantory.YandexSBD;

import com.merantory.YandexSBD.controllers.OrderController;
import com.merantory.YandexSBD.services.OrderService;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private OrderController orderController;

    @Mock
    private OrderService orderService;
}
