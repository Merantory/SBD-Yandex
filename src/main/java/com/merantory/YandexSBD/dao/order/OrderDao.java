package com.merantory.YandexSBD.dao.order;

import com.merantory.YandexSBD.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class OrderDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Order> getOrdersList(int offset, int limit) {
        return jdbcTemplate.query("SELECT * FROM \"order\" OFFSET ? LIMIT ?",
                new OrderDaoRowMapper(),
                offset,
                limit);
    }

    // Append data to orders that has ids
    public List<Order> enrichOrders(List<Order> orderWithIdsList) {
        // Insert into sql query "?," than count equal orderList size;
        String inSql = String.join(",", Collections.nCopies(orderWithIdsList.size(), "?"));
        String sqlQuery = String.format("SELECT * FROM \"order\" WHERE order_id IN (%s)", inSql);
        Long[] ordersIds = new Long[orderWithIdsList.size()];
        for (int i = 0; i < orderWithIdsList.size(); i++) {
            ordersIds[i] = orderWithIdsList.get(i).getId();
        }

        return jdbcTemplate.query(sqlQuery, new OrderDaoRowMapper(), ordersIds);
    }

    public Order getOrder(long orderId) {
        return jdbcTemplate.queryForObject("SELECT * FROM \"order\" WHERE order_id=? LIMIT 1",
                new OrderDaoRowMapper(),
                orderId);
    }

    public void save(Order orderForSaving) {
        String sqlQuery = "INSERT INTO \"order\"(weight, regions, delivery_hours, cost) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                orderForSaving.getWeight(),
                orderForSaving.getRegions(),
                orderForSaving.getDeliveryHours(),
                orderForSaving.getCost());
    }

    public void save(List<Order> ordersForSaving) {
        String sqlQuery = "INSERT INTO \"order\"(weight, regions, delivery_hours, cost) VALUES(?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setFloat(1, ordersForSaving.get(i).getWeight());
                ps.setLong(2, ordersForSaving.get(i).getRegions());
                ps.setString(3, ordersForSaving.get(i).getDeliveryHours());
                ps.setInt(4, ordersForSaving.get(i).getCost());
            }

            @Override
            public int getBatchSize() {
                return ordersForSaving.size();
            }
        });
    }

    public void markAsCompleted(Order completedOrder) {
        String sqlQuery = "UPDATE \"order\" SET completed_time = COALESCE(completed_time, ?)," +
                " courier_deliver_id = COALESCE(courier_deliver_id, ?) WHERE order_id = ?";
        jdbcTemplate.update(sqlQuery, new OrderDaoRowMapper(),
                completedOrder.getCompletedTime(),
                completedOrder.getDeliveryCourierId(),
                completedOrder.getId());
    }

    public void markAsCompleted(List<Order> completedOrderList) {
        String sqlQuery = "UPDATE \"order\" SET completed_time = COALESCE(completed_time, ?)," +
                " courier_deliver_id = COALESCE(courier_deliver_id, ?) WHERE order_id = ?";

        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                // bind params to prepare statement.
                ps.setTimestamp(1, Timestamp.from(completedOrderList.get(i).getCompletedTime()));
                ps.setLong(2, completedOrderList.get(i).getDeliveryCourierId());
                ps.setLong(3, completedOrderList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return completedOrderList.size();
            }
        });
    }

    // Return completed orders by courier in date period include date boarders
    public List<Order> getCourierOrdersPerDatePeriod(long courierId, LocalDate startDate, LocalDate endDate) {
        String sqlQuery = "SELECT * FROM \"order\" WHERE courier_deliver_id = ? AND (completed_time BETWEEN ? AND ?)";
        return jdbcTemplate.query(sqlQuery, new OrderDaoRowMapper(), courierId, startDate, endDate);
    }
}
