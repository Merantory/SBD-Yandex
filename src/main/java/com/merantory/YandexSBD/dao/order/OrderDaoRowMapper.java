package com.merantory.YandexSBD.dao.order;

import com.merantory.YandexSBD.models.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class OrderDaoRowMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();

        order.setId(rs.getLong("order_id"));
        order.setWeight(rs.getFloat("weight"));
        order.setRegions(rs.getInt("regions"));
        order.setDeliveryHours(rs.getString("delivery_hours"));
        order.setCost(rs.getInt("cost"));
        order.setDeliveryCourierId(rs.getLong("courier_deliver_id"));

        Timestamp completedTime = rs.getTimestamp("completed_time");
        if (completedTime != null)
            order.setCompletedTime(completedTime.toInstant());

        return order;
    }
}
