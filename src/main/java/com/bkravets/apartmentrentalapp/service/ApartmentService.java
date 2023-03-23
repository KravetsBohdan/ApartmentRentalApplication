package com.bkravets.apartmentrentalapp.service;

import com.bkravets.apartmentrentalapp.dto.ApartmentDto;
import com.bkravets.apartmentrentalapp.dto.BookingDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ApartmentService {

    ApartmentDto getApartment(long id);

    ApartmentDto addApartment(ApartmentDto apartmentDTO);

    ApartmentDto updateApartment(long apartmentId, ApartmentDto apartmentDTO);

    void deleteApartment(long apartmentId);

    List<LocalDate> getBookedDaysByApartmentId(long id);

    Page<ApartmentDto> getAllApartments(int page, int size,  String city, String query, String sortBy, String sortDir);

    List<BookingDto> getBookingsByApartmentId(long apartmentId);
}
