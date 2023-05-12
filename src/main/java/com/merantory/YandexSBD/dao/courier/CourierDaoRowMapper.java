package com.merantory.YandexSBD.dao.courier;

import com.merantory.YandexSBD.models.Courier;
import com.merantory.YandexSBD.models.CourierType;
import com.merantory.YandexSBD.models.CourierTypeEnum;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourierDaoRowMapper implements RowMapper<Courier> {
    @Override
    public Courier mapRow(ResultSet rs, int rowNum) throws SQLException {
        Courier courier = new Courier();
        // Set courier fields
        courier.setCourierId(rs.getInt("courier_id"));
        courier.setCourierType(new CourierType(CourierTypeEnum.valueOf(rs.getString("courier_type")),
                rs.getInt("income_coefficient"),
                rs.getInt("rate_coefficient"),
                rs.getInt("max_weight"),
                rs.getInt("max_orders_count"),
                rs.getInt("max_regions_count")));
        courier.addRegion(rs.getInt("region_id"));
        courier.addWorkingHours(rs.getString("time_interval"));
        // Set a bunch of courier regions and time interval, until we have rows with this courier
        while (rs.next() && !rs.isAfterLast()) {
            courier.addRegion(rs.getInt("region_id"));
            courier.addWorkingHours(rs.getString("time_interval"));
        }

        return courier;
    }
}
