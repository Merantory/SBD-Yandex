package com.merantory.YandexSBD.dto.order;

import com.merantory.YandexSBD.models.Order;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class OrderConverter {

    private OrderConverter() {
        throw new AssertionError();
    }

    public static List<OrderDto> convertOrderListToOrderDtoList(List<Order> orders) {
        List<OrderDto> orderDtoList = new ArrayList<>();
        for (Order order : orders) {
            orderDtoList.add(convertOrderToOrderDto(order));
        }

        return orderDtoList;
    }

    public static OrderDto convertOrderToOrderDto(Order sourceOrder) {
        Converter<String, List> stringToListConverter = c -> Collections.singletonList(c.getSource());

        ModelMapper modelMapper = new ModelMapper();
        TypeMap<Order, OrderDto> propertyMapper = modelMapper.createTypeMap(Order.class, OrderDto.class);

        propertyMapper.addMapping(Order::getId, OrderDto::setId);
        // Convert string of delivery hours to string's list of delivery hours
        propertyMapper.addMappings(mapper -> mapper.using(stringToListConverter)
                .map(Order::getDeliveryHours, OrderDto::setDeliveryHours));
        propertyMapper.addMapping(Order::getCompletedTime, OrderDto::setCompletedTime);

        OrderDto mappedOrderDto = modelMapper.map(sourceOrder, OrderDto.class);
        return mappedOrderDto;
    }

    public static List<Order> convertCreateOrderDtoListToOrderList(List<CreateOrderDto> createOrderDtoList) {
        List<Order> orderList = new ArrayList<>();
        for (CreateOrderDto createOrderDto : createOrderDtoList) {
            orderList.add(convertCreateOrderDtoToOrder(createOrderDto));
        }

        return orderList;
    }

    public static Order convertCreateOrderDtoToOrder(CreateOrderDto createOrderDto) {
        Converter<List, String> ListToStringConverter = c -> String.valueOf(c.getSource().get(0));

        ModelMapper modelMapper = new ModelMapper();
        TypeMap<CreateOrderDto, Order> propertyMapper = modelMapper.createTypeMap(CreateOrderDto.class, Order.class);

        // Simplify string's list to string
        propertyMapper.addMappings(mapper -> mapper.using(ListToStringConverter)
                .map(CreateOrderDto::getDeliveryHours, Order::setDeliveryHours));

        Order order = modelMapper.map(createOrderDto, Order.class);
        return order;
    }

    public static Order convertCompleteOrderDtoToOrder(CompleteOrderDto completeOrderDto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        TypeMap<CompleteOrderDto, Order> propertyMapper = modelMapper.createTypeMap(CompleteOrderDto.class, Order.class);

        propertyMapper.addMapping(CompleteOrderDto::getOrderId, Order::setId);
        propertyMapper.addMapping(CompleteOrderDto::getCourierId, Order::setDeliveryCourierId);
        propertyMapper.addMapping(CompleteOrderDto::getCompleteTime, Order::setCompletedTime);

        Order order = modelMapper.map(completeOrderDto, Order.class);
        return order;
    }

    public static List<Order> convertCompleteOrderDtoListToOrderList(List<CompleteOrderDto> completeOrderDtoList) {
        List<Order> orderList = new ArrayList<>();
        for (CompleteOrderDto completeOrderDto : completeOrderDtoList) {
            orderList.add(convertCompleteOrderDtoToOrder(completeOrderDto));
        }
        return orderList;
    }
}
