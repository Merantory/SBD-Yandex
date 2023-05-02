package com.merantory.YandexSBD.dto.courier;

import com.merantory.YandexSBD.dto.courier.responses.ResponseCourierMetaInfo;
import com.merantory.YandexSBD.models.Courier;
import com.merantory.YandexSBD.models.CourierType;
import com.merantory.YandexSBD.models.CourierTypeEnum;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import java.util.ArrayList;
import java.util.List;

public final class CourierConverter {

    private CourierConverter() {
        throw new AssertionError();
    }

    public static List<CourierDto> convertCourierListToCourierDtoList(List<Courier> courierList) {
        List<CourierDto> courierDtoList = new ArrayList<>();
        for (Courier courier : courierList) {
            courierDtoList.add(convertCourierToCourierDto(courier));
        }

        return courierDtoList;
    }
    public static CourierDto convertCourierToCourierDto(Courier sourceCourier) {
        Converter<CourierType, CourierTypeEnum> courierTypeToEnum = c -> c.getSource().getType();

        ModelMapper modelMapper = new ModelMapper();
        TypeMap<Courier, CourierDto> propertyMapper = modelMapper.createTypeMap(Courier.class, CourierDto.class);

        propertyMapper.addMappings(mapper -> mapper.using(courierTypeToEnum)
                .map(Courier::getCourierType, CourierDto::setCourierType));

        CourierDto mappedCourierDto = modelMapper.map(sourceCourier, CourierDto.class);
        return mappedCourierDto;
    }

    public static List<Courier> convertCreateCourierDtoListToCourierList(List<CreateCourierDto> createCourierDtoList) {
        List<Courier> courierList = new ArrayList<>();
        for (CreateCourierDto createCourierDto : createCourierDtoList) {
            courierList.add(convertCreateCourierDtoToCourier(createCourierDto));
        }

        return courierList;
    }

    public static Courier convertCreateCourierDtoToCourier(CreateCourierDto createCourierDto) {
        Converter<CourierTypeEnum, CourierType> TypeEnumToCourierType = c -> new CourierType(c.getSource());

        ModelMapper modelMapper = new ModelMapper();
        TypeMap<CreateCourierDto, Courier> propertyMapper = modelMapper.createTypeMap(CreateCourierDto.class, Courier.class);

        propertyMapper.addMappings(mapper -> mapper.using(TypeEnumToCourierType)
                .map(CreateCourierDto::getCourierType, Courier::setCourierType));

        Courier mappedCourier = modelMapper.map(createCourierDto, Courier.class);

        return mappedCourier;
    }

    public static ResponseCourierMetaInfo convertCourierToResponseCourierMetaInfo(Courier courier) {
        return new ResponseCourierMetaInfo(courier.getCourierId(),
                CourierTypeEnum.valueOf(courier.getCourierType().getType().toString()),
                courier.getRegions(),
                courier.getWorkingHours(),
                courier.getRating(),
                courier.getEarnings());
    }
}
