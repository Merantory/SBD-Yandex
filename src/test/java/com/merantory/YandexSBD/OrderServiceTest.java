package com.merantory.YandexSBD;

import com.merantory.YandexSBD.dao.order.OrderDao;
import com.merantory.YandexSBD.models.Order;
import com.merantory.YandexSBD.services.OrderService;
import com.merantory.YandexSBD.util.exceptions.order.OrderNotMarkCompleteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class OrderServiceTest {
    @Mock
    private OrderDao orderDao;

    private OrderService orderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderDao);
    }

    @Test
    public void markAsCompletedTest() {
        List<Order> completedOrderList = new ArrayList<>();
        completedOrderList.add(new Order());
        when(orderDao.enrichOrders(completedOrderList)).thenReturn(completedOrderList);

        List<Order> result = orderService.markAsCompleted(completedOrderList);

        assertEquals(completedOrderList, result);
        verify(orderDao, times(1)).markAsCompleted(completedOrderList);
        verify(orderDao, times(1)).enrichOrders(completedOrderList);
    }

    @Test
    public void markAsCompletedFailTest() {
        List<Order> completedOrderList = new ArrayList<>();
        when(orderDao.enrichOrders(completedOrderList)).thenReturn(new ArrayList<>());

        assertThrows(OrderNotMarkCompleteException.class, () -> orderService.markAsCompleted(completedOrderList));
        verify(orderDao, times(1)).markAsCompleted(completedOrderList);
        verify(orderDao, times(1)).enrichOrders(completedOrderList);
    }
}
