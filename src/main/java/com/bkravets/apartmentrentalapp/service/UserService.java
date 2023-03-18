package com.bkravets.apartmentrentalapp.service;

import com.bkravets.apartmentrentalapp.dto.*;
import com.bkravets.apartmentrentalapp.entity.User;

import java.util.List;

public interface UserService {
    User getLoggedUser();

    UserDto getCurrentUser();

    UserDto create(UserDto userDTO);

    AuthResponse login(AuthRequest authRequest);

    UserDto update(UserDto userDTO);

    List<ApartmentDto> getUserApartments();

    List<BookingDto> getUserBookings();
}

