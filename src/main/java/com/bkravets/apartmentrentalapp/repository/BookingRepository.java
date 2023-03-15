package com.bkravets.apartmentrentalapp.repository;

import com.bkravets.apartmentrentalapp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByApartmentId(long apartmentId);

    Optional<Booking> findByTenantIdAndApartmentId(Long id, long apartmentId);

    List<Booking> findAllByEndDateBefore(LocalDate date);
}
