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
}
