package com.merantory.YandexSBD.controllers;

import com.merantory.YandexSBD.dto.order.OrderDto;
import com.merantory.YandexSBD.dto.order.OrderConverter;
import com.merantory.YandexSBD.dto.order.requests.RequestCompleteOrderDto;
import com.merantory.YandexSBD.dto.order.requests.RequestCreateOrder;
import com.merantory.YandexSBD.models.Order;
import com.merantory.YandexSBD.services.OrderService;
import com.merantory.YandexSBD.util.exceptions.order.OrderInvalidRequestParamsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderDto> getOrdersList(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "1") int limit) {

        if (offset < 0 || limit < 1) {
            String errorMessage = "Invalid query params given. offset = " + offset + " limit = " + limit;
            throw new OrderInvalidRequestParamsException(errorMessage);
        }

        List<Order> orderList = orderService.getOrdersList(offset, limit);
        List<OrderDto> ordersDtoList = OrderConverter.convertOrderListToOrderDtoList(orderList);

        return ordersDtoList;
    }

    @GetMapping("/{order_id}")
    public OrderDto getOrder(@PathVariable("order_id") long orderId) {
        Order order = orderService.getOrder(orderId);
        OrderDto responseOrder = OrderConverter.convertOrderToOrderDto(order);
        return responseOrder;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> saveOrder(@RequestBody @Valid RequestCreateOrder requestCreateOrder) {
        List<Order> orderToSaveList = OrderConverter.convertCreateOrderDtoListToOrderList(requestCreateOrder.orders());
        orderService.save(orderToSaveList);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/complete")
    public ResponseEntity<List<OrderDto>> markOrderAsCompleted(@RequestBody @Valid RequestCompleteOrderDto requestCompleteOrderDto) {
        List<Order> completedOrder = OrderConverter.convertCompleteOrderDtoListToOrderList(
                requestCompleteOrderDto.completeOrderDto());
        List<OrderDto> orderDtoList = OrderConverter.convertOrderListToOrderDtoList(
                orderService.markAsCompleted(completedOrder));

        return new ResponseEntity<>(orderDtoList, HttpStatus.OK);
    }
}
