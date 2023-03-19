package com.bkravets.apartmentrentalapp.repository;

import com.bkravets.apartmentrentalapp.entity.Booking;
import com.bkravets.apartmentrentalapp.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByApartmentId(long apartmentId);

    Optional<Booking> findByTenantIdAndApartmentId(long id, long apartmentId);

    List<Booking> findAllByEndDateBeforeOrStatus(LocalDate date, Status status);
}
