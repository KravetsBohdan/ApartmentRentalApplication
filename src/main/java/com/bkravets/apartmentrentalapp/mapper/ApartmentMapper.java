package com.bkravets.apartmentrentalapp.mapper;

import com.bkravets.apartmentrentalapp.dto.ApartmentDto;
import com.bkravets.apartmentrentalapp.entity.Apartment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface ApartmentMapper {
    ApartmentMapper INSTANCE = Mappers.getMapper(ApartmentMapper.class);

    ApartmentDto toDTO(Apartment apartment);

    Apartment toEntity(ApartmentDto apartmentDto);
}

