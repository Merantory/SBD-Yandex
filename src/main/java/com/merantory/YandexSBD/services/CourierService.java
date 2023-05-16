package com.merantory.YandexSBD.services;

import com.merantory.YandexSBD.dao.courier.CourierDao;
import com.merantory.YandexSBD.dao.order.OrderDao;
import com.merantory.YandexSBD.models.Courier;
import com.merantory.YandexSBD.models.Order;
import com.merantory.YandexSBD.util.exceptions.courier.CourierNotFoundException;
import com.merantory.YandexSBD.util.exceptions.order.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CourierService {
    private final CourierDao courierDao;
    private final OrderDao orderDao;

    @Autowired
    public CourierService(CourierDao courierDao, OrderDao orderDao) {
        this.courierDao = courierDao;
        this.orderDao = orderDao;
    }

    public List<Courier> getCouriersList(int offset, int limit) {
        List<Courier> courierList = courierDao.getCouriersList(offset, limit);
        if (courierList.isEmpty()) {
            String errorMessage = "Couriers not found";
            throw new CourierNotFoundException(errorMessage);
        }
        return courierList;
    }

    public Courier getCourier(long courierId) {
        try {
            return courierDao.getCourier(courierId);
        } catch (EmptyResultDataAccessException exception) {
            String errorMessage = "Courier with id = " + courierId + " not found";
            throw new CourierNotFoundException(errorMessage);
        }
    }

    @Transactional
    public List<Courier> save(List<Courier> courierListForSaving) {
        for (Courier courier : courierListForSaving) {
            save(courier);
        }
        return courierListForSaving;
    }

    @Transactional
    public Courier save(Courier courierForSaving) {
        return courierDao.save(courierForSaving);
    }

    public List<Order> getCourierOrdersPerDatePeriod(long courierId, LocalDate startDate, LocalDate endDate) {
        return orderDao.getCourierOrdersPerDatePeriod(courierId, startDate, endDate);
    }

    public Courier setCourierMetaInfoPerDatePeriod(Courier courier, LocalDate startDate, LocalDate endDate) {
        courier.setCompleteOrders(getCourierOrdersPerDatePeriod(courier.getCourierId(), startDate, endDate));
        if (!courier.getCompleteOrders().isEmpty()) {
            courier.setRating(calculateCourierRating(courier, startDate, endDate));
            courier.setEarnings(calculateCourierEarning(courier));
        } else throw new OrderNotFoundException("Courier doesnt complete any orders per period");

        return courier;
    }

    private int calculateCourierRating(Courier courier, LocalDate startDate, LocalDate endDate) {
        Duration duration = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay());
        int intervalHours = (int) Math.abs(duration.toHours());

        int rating = 0;
        if (intervalHours != 0) {
            // Calculate courier rating. (Complete orders count / hours between dates * rating_coefficient_of_type)
            rating = (int) ((float) courier.getCompleteOrders().size() /
                    intervalHours * courier.getCourierType().getRateCoefficient());
        }
        return rating;
    }

    private int calculateCourierEarning(Courier courier) {
        int earning = 0;
        for (Order courierOrder : courier.getCompleteOrders()) {
            earning += courierOrder.getCost() * courier.getCourierType().getIncomeCoefficient();
        }
        return earning;
    }
}
