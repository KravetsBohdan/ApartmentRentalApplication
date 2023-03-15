package com.bkravets.apartmentrentalapp.controller;

import com.bkravets.apartmentrentalapp.dto.BookingDto;
import com.bkravets.apartmentrentalapp.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/{apartmentId}")
    public ResponseEntity<BookingDto> addBooking(@Valid @RequestBody BookingDto bookingDTO,
                                                 @PathVariable long apartmentId) {
        BookingDto createdBooking = bookingService.addBooking(bookingDTO, apartmentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @PatchMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingDto> confirmBooking(@PathVariable long bookingId) {
        BookingDto confirmedBooking = bookingService.confirmBooking(bookingId);
        return ResponseEntity.ok(confirmedBooking);
    }

    @PatchMapping("/{bookingId}/reject")
    public ResponseEntity<BookingDto> rejectBooking(@PathVariable long bookingId) {
        BookingDto rejectedBooking = bookingService.rejectBooking(bookingId);
        return ResponseEntity.ok(rejectedBooking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable long bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}