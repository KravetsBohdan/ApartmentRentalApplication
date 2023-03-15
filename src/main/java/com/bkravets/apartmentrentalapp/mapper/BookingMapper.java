package com.bkravets.apartmentrentalapp.mapper;

import com.bkravets.apartmentrentalapp.dto.BookingDto;
import com.bkravets.apartmentrentalapp.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "city", source = "apartment.city")
    @Mapping(target = "location", source = "apartment.location")
    @Mapping(target = "status", source = "status")
    BookingDto toDTO(Booking booking);

    Booking toEntity(BookingDto bookingDTO);

    List<BookingDto> toDTOs(List<Booking> bookings);
}

