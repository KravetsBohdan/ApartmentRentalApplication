package com.bkravets.apartmentrentalapp.service;

import com.bkravets.apartmentrentalapp.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingDto bookingDTO, long apartmentId);

    BookingDto confirmBooking(long bookingId);

    BookingDto rejectBooking(long bookingId);

    void deleteBooking(long bookingId);

    List<BookingDto> getBookingsByApartmentId(long apartmentId);
}

