package com.merantory.YandexSBD.controllers;

import com.merantory.YandexSBD.dto.courier.CourierConverter;
import com.merantory.YandexSBD.dto.courier.CourierDto;
import com.merantory.YandexSBD.dto.courier.responses.ResponseCourierDto;
import com.merantory.YandexSBD.models.Courier;
import com.merantory.YandexSBD.services.CourierService;
import com.merantory.YandexSBD.util.exceptions.courier.CourierInvalidRequestParamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/couriers")
public class CourierController {
    private final CourierService courierService;

    @Autowired
    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @GetMapping
    public ResponseEntity<ResponseCourierDto> getCouriersList(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                              @RequestParam(value = "limit", defaultValue = "1") int limit) {
        if (offset < 0 || limit < 1) {
            String errorMessage = "Invalid query params given. offset = " + offset + " limit = " + limit;
            throw new CourierInvalidRequestParamsException(errorMessage);
        }


        List<Courier> courierList = courierService.getCouriersList(offset, limit);
        List<CourierDto> courierDtoList = CourierConverter.convertCourierListToCourierDtoList(courierList);
        ResponseCourierDto responseCourierDto = new ResponseCourierDto(courierDtoList, offset, limit);

        return new ResponseEntity<>(responseCourierDto, HttpStatus.OK);
    }

    @GetMapping("/{courier_id}")
    public ResponseEntity<CourierDto> getCourier(@PathVariable("courier_id") long courierId) {
        Courier courier = courierService.getCourier(courierId);
        CourierDto courierDto = CourierConverter.convertCourierToCourierDto(courier);

        return new ResponseEntity<>(courierDto, HttpStatus.OK);
    }
}