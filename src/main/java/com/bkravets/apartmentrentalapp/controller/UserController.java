package com.bkravets.apartmentrentalapp.controller;

import com.bkravets.apartmentrentalapp.dto.*;
import com.bkravets.apartmentrentalapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDto userDTO = userService.getCurrentUser();
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/me/apartments")
    public ResponseEntity<List<ApartmentDto>> getUserApartments() {
        List<ApartmentDto> apartments = userService.getUserApartments();
        return ResponseEntity.ok(apartments);
    }

    @GetMapping("/me/bookings")
    public ResponseEntity<List<BookingDto>> getUserBookings() {
        List<BookingDto> bookings = userService.getUserBookings();
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDTO) {
        UserDto createdUser = userService.create(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        AuthResponse response = userService.login(authRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDTO) {
        UserDto updatedUser = userService.update(userDTO);
        return ResponseEntity.accepted().body(updatedUser);
    }
}

