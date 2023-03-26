package com.bkravets.apartmentrentalapp.controller;

import com.bkravets.apartmentrentalapp.dto.ApartmentDto;
import com.bkravets.apartmentrentalapp.dto.BookingDto;
import com.bkravets.apartmentrentalapp.service.ApartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/apartments")
@CrossOrigin(origins = "http://localhost:4200")
public class ApartmentController {
    private final ApartmentService apartmentService;

    @GetMapping("/{apartmentId}/booked-days")
    public ResponseEntity<List<LocalDate>> getBookedDays(@PathVariable long apartmentId) {
        List<LocalDate> bookedDays = apartmentService.getBookedDaysByApartmentId(apartmentId);

        return ResponseEntity.ok(bookedDays);
    }

    @GetMapping("/{apartmentId}/bookings")
    public ResponseEntity<List<BookingDto>> getBookingsByApartmentId(@PathVariable long apartmentId) {
        List<BookingDto> bookings = apartmentService.getBookingsByApartmentId(apartmentId);

        return ResponseEntity.ok(bookings);
    }

    @GetMapping
    public ResponseEntity<Page<ApartmentDto>> getAllApartments(@RequestParam(required = false) String query,
                                                               @RequestParam(required = false) String city,
                                                               @RequestParam(required = false, defaultValue = "0") int page,
                                                               @RequestParam(required = false, defaultValue = "3") int size,
                                                               @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                               @RequestParam(required = false, defaultValue = "asc") String sortDir) {

        Page<ApartmentDto> apartments = apartmentService.getAllApartments(query, city, page, size, sortBy, sortDir);
        return ResponseEntity.ok(apartments);
    }

    @GetMapping("/{apartmentId}")
    public ResponseEntity<ApartmentDto> getApartment(@PathVariable long apartmentId) {
        ApartmentDto apartment = apartmentService.getApartment(apartmentId);

        return ResponseEntity.ok(apartment);
    }

    @PostMapping
    public ResponseEntity<ApartmentDto> addApartment(@Valid @RequestBody ApartmentDto apartment) {
        ApartmentDto createdApartment = apartmentService.addApartment(apartment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdApartment);
    }

    @PutMapping("/{apartmentId}")
    public ResponseEntity<ApartmentDto> updateApartment(@PathVariable long apartmentId,
                                                        @Valid @RequestBody ApartmentDto apartmentDto) {
        ApartmentDto updatedApartment = apartmentService.updateApartment(apartmentId, apartmentDto);

        return ResponseEntity.ok(updatedApartment);
    }

    @DeleteMapping("/{apartmentId}")
    public ResponseEntity<Void> deleteApartment(@PathVariable long apartmentId) {
        apartmentService.deleteApartment(apartmentId);
        return ResponseEntity.noContent().build();
    }
}
