package com.bkravets.apartmentrentalapp.repository;

import com.bkravets.apartmentrentalapp.entity.Apartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    @Query("""
            SELECT a FROM Apartment a WHERE
            a.title LIKE CONCAT('%',:query, '%')
            and a.city LIKE CONCAT('%', :city, '%')
            """)
    Page<Apartment> findAllByTitleContainingAndCityContaining(@Param("query") String query,@Param("city") String city, Pageable pageable);
}
