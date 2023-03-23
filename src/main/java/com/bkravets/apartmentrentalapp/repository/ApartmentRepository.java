package com.bkravets.apartmentrentalapp.repository;

import com.bkravets.apartmentrentalapp.entity.Apartment;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    Page<Apartment> findByCityAndTitleContaining(String city, String query, Pageable pageable);

    Page<Apartment> findByCity(String city, Pageable pageable);

    Page<Apartment> findByTitleContaining(String query, Pageable pageable);
}
