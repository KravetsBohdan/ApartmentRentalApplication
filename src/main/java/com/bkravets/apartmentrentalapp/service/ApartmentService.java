package com.bkravets.apartmentrentalapp.service;

import com.bkravets.apartmentrentalapp.dto.ApartmentDto;
import com.bkravets.apartmentrentalapp.dto.BookingDto;
import com.bkravets.apartmentrentalapp.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ApartmentService {

    ApartmentDto getApartment(long id);

    ApartmentDto addApartment(ApartmentDto apartmentDTO);

    ApartmentDto updateApartment(long apartmentId, ApartmentDto apartmentDTO);

    void deleteApartment(long apartmentId);

    List<LocalDate> getBookedDaysByApartmentId(long id);

    Page<ApartmentDto> getAllApartments(String city, String query, String sortBy, String sortDir, Pageable pageable);

    List<BookingDto> getBookingsByApartmentId(long apartmentId);
}
