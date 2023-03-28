package com.bkravets.apartmentrentalapp.repository;

import com.bkravets.apartmentrentalapp.entity.Booking;
import com.bkravets.apartmentrentalapp.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByApartmentId(long apartmentId);

    List<Booking> findAllByEndDateBeforeOrStatus(LocalDate date, Status status);
}
