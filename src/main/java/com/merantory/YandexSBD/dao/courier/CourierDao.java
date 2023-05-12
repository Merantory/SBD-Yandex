package com.merantory.YandexSBD.dao.courier;

import com.merantory.YandexSBD.models.Courier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
public class CourierDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CourierDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Courier> getCouriersList(long offset, long limit) {
        String sqlQuery = "SELECT co.courier_id, co.courier_type," +
                " ct.income_coefficient, ct.rate_coefficient, ct.max_weight, ct.max_orders_count, ct.max_regions_count," +
                " cs.time_interval, cr.region_id FROM" +
                " (SELECT * FROM courier OFFSET ? LIMIT ?) AS co" +
                " LEFT JOIN courier_schedule AS cs ON co.courier_id = cs.courier_id" +
                " LEFT JOIN courier_region AS cr on co.courier_id = cr.courier_id" +
                " LEFT JOIN courier_type AS ct on co.courier_type = ct.type_name";
        return jdbcTemplate.query(sqlQuery, new CourierDaoResultSetExtractor(), offset, limit);
    }

    public Courier getCourier(long courierId) {
        String sqlQuery = "SELECT co.courier_id, co.courier_type," +
                " ct.income_coefficient, ct.rate_coefficient, ct.max_weight, ct.max_orders_count, ct.max_regions_count," +
                " cs.time_interval, cr.region_id FROM" +
                " (SELECT * FROM courier WHERE courier_id = ? LIMIT 1) AS co" +
                " LEFT JOIN courier_schedule AS cs ON co.courier_id = cs.courier_id" +
                " LEFT JOIN courier_region AS cr on co.courier_id = cr.courier_id" +
                " LEFT JOIN courier_type AS ct on co.courier_type = ct.type_name";
        return jdbcTemplate.queryForObject(sqlQuery, new CourierDaoRowMapper(), courierId);
    }

    public Courier save(Courier courierForSaving) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO courier(courier_type) VALUES(?)";
        jdbcTemplate.update(connection -> {
            // Get id from field courier_id after insert
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"courier_id"});
            ps.setString(1, courierForSaving.getCourierType().getType().toString());

            return ps;
        }, keyHolder);
        // Get courier id that contains in keyHolder after query-update.
        long courierId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        courierForSaving.setCourierId(courierId);
        // Call for enrich courier
        updateCourierRegions(courierForSaving);
        saveSchedule(courierForSaving);
        updateCourierSchedules(courierForSaving);

        return courierForSaving;
    }

    private void updateCourierRegions(Courier courierWithRegions) {
        String sqlQuery = "INSERT INTO courier_region(courier_id, region_id) VALUES(?, ?)";

        int regionsCount = courierWithRegions.getRegions().size();
        Long[] regions = courierWithRegions.getRegions().toArray(new Long[regionsCount]);

        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                // bind params to prepare statement.
                ps.setLong(1, courierWithRegions.getCourierId());
                ps.setLong(2, regions[i]);
            }

            @Override
            public int getBatchSize() {
                return regionsCount;
            }
        });
    }

    private void saveSchedule(Courier courierWithSchedule) {
        String sqlQuery = "INSERT INTO schedule(time_interval) VALUES (?) ON CONFLICT DO NOTHING";
        int workingHoursCount = courierWithSchedule.getWorkingHours().size();
        String[] workingHours = courierWithSchedule.getWorkingHours().toArray(new String[workingHoursCount]);

        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                // bind params to prepare statement.
                ps.setString(1, workingHours[i]);
            }

            @Override
            public int getBatchSize() {
                return workingHoursCount;
            }
        });
    }

    private void updateCourierSchedules(Courier courierWithSchedule) {
        String sqlQuery = "INSERT INTO courier_schedule(courier_id, time_interval) VALUES (?, ?)";
        int workingHoursCount = courierWithSchedule.getWorkingHours().size();
        String[] workingHours = courierWithSchedule.getWorkingHours().toArray(new String[workingHoursCount]);

        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                // bind params to prepare statement.
                ps.setLong(1, courierWithSchedule.getCourierId());
                ps.setString(2, workingHours[i]);
            }

            @Override
            public int getBatchSize() {
                return workingHoursCount;
            }
        });
    }
}
