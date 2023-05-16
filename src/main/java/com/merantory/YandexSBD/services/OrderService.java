package com.merantory.YandexSBD.services;

import com.merantory.YandexSBD.dao.order.OrderDao;
import com.merantory.YandexSBD.models.Order;
import com.merantory.YandexSBD.util.exceptions.order.OrderNotFoundException;
import com.merantory.YandexSBD.util.exceptions.order.OrderNotMarkCompleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderDao orderDao;

    @Autowired
    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public List<Order> getOrdersList(int offset, int limit) {
        List<Order> orders = orderDao.getOrdersList(offset, limit);
        if (orders.isEmpty()) {
            String errorMessage = "Orders not found";
            throw new OrderNotFoundException(errorMessage);
        }
        return orders;
    }

    public Order getOrder(long orderId) {
        try {
            return orderDao.getOrder(orderId);
        } catch (EmptyResultDataAccessException exception) {
            throw new OrderNotFoundException("Order with id = " + orderId + " not found");
        }
    }

    @Transactional
    public void save(List<Order> ordersListForSaving) {
        orderDao.save(ordersListForSaving);
    }

    @Transactional
    public void save(Order orderForSaving) {
        orderDao.save(orderForSaving);
    }

    @Transactional
    public List<Order> markAsCompleted(List<Order> completedOrderList) {
        orderDao.markAsCompleted(completedOrderList);
        completedOrderList = orderDao.enrichOrders(completedOrderList);
        if (completedOrderList.isEmpty()) {
            String errorMessage = "Order mark as complete fail";
            throw new OrderNotMarkCompleteException(errorMessage);
        }
        return completedOrderList;
    }

    @Transactional
    public Order markAsCompleted(Order completedOrder) {
        orderDao.markAsCompleted(completedOrder);
        return orderDao.getOrder(completedOrder.getId());
    }
}
