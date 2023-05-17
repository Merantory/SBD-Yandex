package com.merantory.YandexSBD;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.merantory.YandexSBD.controllers.CourierController;
import com.merantory.YandexSBD.controllers.CustomGlobalExceptionHandler;
import com.merantory.YandexSBD.dto.courier.CreateCourierDto;
import com.merantory.YandexSBD.dto.courier.requests.RequestCreateCourier;
import com.merantory.YandexSBD.models.Courier;
import com.merantory.YandexSBD.models.CourierType;
import com.merantory.YandexSBD.models.CourierTypeEnum;
import com.merantory.YandexSBD.models.Order;
import com.merantory.YandexSBD.services.CourierService;
import com.merantory.YandexSBD.util.exceptions.courier.CourierInvalidRequestParamsException;
import com.merantory.YandexSBD.util.exceptions.courier.CourierMetaInfoInvalidDateException;
import com.merantory.YandexSBD.util.exceptions.courier.CourierNotFoundException;
import com.merantory.YandexSBD.util.exceptions.order.OrderNotFoundException;
import org.junit.experimental.results.ResultMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
class CourierControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CourierService courierService;

    private CourierController courierController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        courierController = new CourierController(courierService);
        mockMvc = MockMvcBuilders.standaloneSetup(courierController)
                .setControllerAdvice(CustomGlobalExceptionHandler.class)
                .build();
    }

    @Test
    public void getCouriersListTest() throws Exception {
        int defaultOffset = 0;
        int defaultLimit = 1;

        mockMvc.perform(get("/couriers"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(courierService, times(1)).getCouriersList(defaultOffset, defaultLimit);
    }

    @Test
    public void invalidPathParamsGetCouriersListTest() throws Exception {
        String defaultOffset = "-1";
        String defaultLimit = "0";

        mockMvc.perform(get("/couriers")
                        .param("offset", defaultOffset)
                        .param("limit", defaultLimit))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof CourierInvalidRequestParamsException));

        verify(courierService, times(0)).getCouriersList(anyInt(), anyInt());
    }

    @Test
    public void getCourierTest() throws Exception {
        Courier courier = new Courier();
        courier.setCourierId(1L);
        courier.setCourierType(new CourierType(CourierTypeEnum.valueOf("FOOT")));

        when(courierService.getCourier(anyLong())).thenReturn(courier);

        mockMvc.perform(get("/couriers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.courier_id").value(1L));

        verify(courierService, times(1)).getCourier(anyLong());
    }

    @Test
    public void getNotExistCourierTest() throws Exception {
        when(courierService.getCourier(anyLong())).thenThrow(CourierNotFoundException.class);

        mockMvc.perform(get("/couriers/1"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CourierNotFoundException));

        verify(courierService, times(1)).getCourier(anyLong());
    }

    @Test
    public void saveCourierTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        CreateCourierDto courierDto = new CreateCourierDto();
        courierDto.setWorkingHours(Set.of("10:00-16:00", "18:00-21:30"));
        courierDto.setRegions(Set.of(1L, 2L));
        courierDto.setCourierType(CourierTypeEnum.valueOf("FOOT"));

        RequestCreateCourier requestCreateCourier = new RequestCreateCourier(List.of(courierDto));

        Courier expectedCourier = new Courier();
        expectedCourier.setCourierId(1);
        expectedCourier.setWorkingHours(Set.of("10:00-16:00", "18:00-21:30"));
        expectedCourier.setRegions(Set.of(1L, 2L));
        expectedCourier.setCourierType(new CourierType(CourierTypeEnum.valueOf("FOOT")));

        when(courierService.save(anyList())).thenReturn(List.of(expectedCourier));

        mockMvc.perform(post("/couriers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateCourier)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.couriers[0].courier_type", equalToCompressingWhiteSpace("FOOT")))
                .andExpect(jsonPath("$.couriers[0].working_hours", hasItems("10:00-16:00", "18:00-21:30")))
                .andExpect(jsonPath("$.couriers[0].regions", hasItems(1, 2)));

        verify(courierService, times(1)).save(anyList());
    }

    @Test
    public void saveInvalidCourierTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        CreateCourierDto courierDto = new CreateCourierDto();
        courierDto.setWorkingHours(Set.of("10:00-09:00", "18:00-21:30")); // Invalid value
        courierDto.setRegions(Set.of(-1L, 2L)); // Invalid value
        courierDto.setCourierType(CourierTypeEnum.valueOf("FOOT"));

        RequestCreateCourier requestCreateCourier = new RequestCreateCourier(List.of(courierDto));

        mockMvc.perform(post("/couriers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateCourier)))
                .andExpect(status().isBadRequest());

        verify(courierService, times(0)).save(anyList());
    }

    @Test
    public void getCourierMetaInfoWhenCourierDoesntExistTest() throws Exception {
        when(courierService.getCourier(anyLong())).thenThrow(CourierNotFoundException.class);

        String startDate = "2023-01-20";
        String endDate = "2023-01-21";

        mockMvc.perform(get("/couriers/meta-info/1")
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CourierNotFoundException));
        verify(courierService, times(1)).getCourier(anyLong());
        verify(courierService, times(0)).setCourierMetaInfoPerDatePeriod(any(), any(), any());
    }

    @Test
    public void getCourierMetaInfoWithInvalidDatesTest() throws Exception {
        String startDate = "2023-01-23"; // startDate is after endDate = invalid
        String endDate = "2023-01-21";

        mockMvc.perform(get("/couriers/meta-info/1")
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof CourierMetaInfoInvalidDateException));
        verify(courierService, times(0)).getCourier(anyLong());
        verify(courierService, times(0)).setCourierMetaInfoPerDatePeriod(any(), any(), any());
    }

    @Test
    public void getCourierMetaInfoForCourierWithoutAnyOrdersTest() throws Exception {
        Courier courier = new Courier();
        courier.setCourierId(1L);

        String startDate = "2023-01-20";
        String endDate = "2023-01-21";

        when(courierService.getCourier(anyLong())).thenReturn(courier);
        when(courierService.setCourierMetaInfoPerDatePeriod(courier,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)))
                .thenThrow(OrderNotFoundException.class);

        mockMvc.perform(get("/couriers/meta-info/1")
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof OrderNotFoundException));
        verify(courierService, times(1)).getCourier(anyLong());
        verify(courierService, times(1)).setCourierMetaInfoPerDatePeriod(any(), any(), any());
    }

    @Test
    public void getCourierMetaInfoTest() throws Exception {
        Courier courier = new Courier();
        courier.setCourierId(1L);
        courier.setCourierType(new CourierType(CourierTypeEnum.FOOT));

        Order courierOrder = new Order();
        courierOrder.setDeliveryCourierId(1L);
        courierOrder.setId(2L);
        courierOrder.setWeight(10);

        String startDate = "2023-01-20";
        String endDate = "2023-01-21";

        when(courierService.getCourier(anyLong())).thenReturn(courier);
        courier.setCompleteOrders(List.of(courierOrder));
        when(courierService.setCourierMetaInfoPerDatePeriod(courier,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)))
                .thenReturn(courier);

        mockMvc.perform(get("/couriers/meta-info/1")
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courier_id").value(courier.getCourierId()))
                .andExpect(jsonPath("$.courier_type").value(courier.getCourierType().getType().toString()));

        verify(courierService, times(1)).getCourier(anyLong());
        verify(courierService, times(1)).setCourierMetaInfoPerDatePeriod(any(), any(), any());
    }
}
