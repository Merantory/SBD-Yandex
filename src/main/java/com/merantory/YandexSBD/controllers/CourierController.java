package com.merantory.YandexSBD.controllers;

import com.merantory.YandexSBD.dto.courier.CourierConverter;
import com.merantory.YandexSBD.dto.courier.CourierDto;
import com.merantory.YandexSBD.dto.courier.requests.RequestCreateCourier;
import com.merantory.YandexSBD.dto.courier.responses.ResponseCourierDto;
import com.merantory.YandexSBD.dto.courier.responses.ResponseCourierMetaInfo;
import com.merantory.YandexSBD.dto.courier.responses.ResponseCreateCourier;
import com.merantory.YandexSBD.models.Courier;
import com.merantory.YandexSBD.services.CourierService;
import com.merantory.YandexSBD.util.exceptions.courier.CourierInvalidRequestParamsException;
import com.merantory.YandexSBD.util.exceptions.courier.CourierMetaInfoInvalidDateException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping
    public ResponseEntity<ResponseCreateCourier> saveCourier(@RequestBody @Valid RequestCreateCourier requestCreateCourier) {
        List<Courier> courierToSaveList = CourierConverter.convertCreateCourierDtoListToCourierList(
                requestCreateCourier.couriers());
        List<CourierDto> courierDtoList =
                CourierConverter.convertCourierListToCourierDtoList(courierService.save(courierToSaveList));
        ResponseCreateCourier responseCreateCourier = new ResponseCreateCourier(courierDtoList);

        return new ResponseEntity<>(responseCreateCourier, HttpStatus.CREATED);
    }

    @GetMapping("/meta-info/{courier_id}")
    public ResponseEntity<ResponseCourierMetaInfo> getCourierMetaInfo(@PathVariable("courier_id") long courierId,
                                                                      @RequestParam(name = "startDate") LocalDate startDate,
                                                                      @RequestParam(name = "endDate") LocalDate endDate) {
        // If end date is less than startDate
        if (endDate.isBefore(startDate)) {
            String errorMessage = "Start date could not be more than end date." +
                    " Received data: startDate = " + startDate + " endDate = " + endDate;
            throw new CourierMetaInfoInvalidDateException(errorMessage);
        }
        // If dates are equals
        if (endDate.isEqual(startDate)) {
            String errorMessage = "Dates could not be equals." +
                    " Received data: startDate = " + startDate + " endDate = " + endDate;
            throw new CourierMetaInfoInvalidDateException(errorMessage);
        }
        Courier courier = courierService.getCourier(courierId);
        courier = courierService.setCourierMetaInfoPerDatePeriod(courier,
                startDate,
                endDate);

        ResponseCourierMetaInfo responseCourierMetaInfo =
                CourierConverter.convertCourierToResponseCourierMetaInfo(courier);
        return new ResponseEntity<>(responseCourierMetaInfo, HttpStatus.OK);
    }
}