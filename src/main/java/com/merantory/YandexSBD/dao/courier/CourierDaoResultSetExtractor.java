package com.merantory.YandexSBD.dao.courier;

import com.merantory.YandexSBD.models.Courier;
import com.merantory.YandexSBD.models.CourierType;
import com.merantory.YandexSBD.models.CourierTypeEnum;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourierDaoResultSetExtractor implements ResultSetExtractor<List<Courier>> {
    @Override
    public List<Courier> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Courier> couriersList = new ArrayList<>();
        Courier courier = null;

        while (rs.next()) {
            if (courier == null || rs.getLong("courier_id") != courier.getCourierId()) {
                if (courier != null) {
                    couriersList.add(courier);
                }
                courier = new Courier();
                courier.setCourierId(rs.getLong("courier_id"));
                courier.setCourierType(new CourierType(CourierTypeEnum.valueOf(rs.getString("courier_type")),
                        rs.getInt("income_coefficient"),
                        rs.getInt("rate_coefficient"),
                        rs.getInt("max_weight"),
                        rs.getInt("max_orders_count"),
                        rs.getInt("max_regions_count")));
            }
            courier.addRegion(rs.getInt("region_id"));
            courier.addWorkingHours(rs.getString("time_interval"));
        }
        couriersList.add(courier);

        return couriersList;
    }
}
