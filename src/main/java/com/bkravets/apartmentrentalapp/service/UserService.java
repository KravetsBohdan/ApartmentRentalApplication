package com.bkravets.apartmentrentalapp.service;

import com.bkravets.apartmentrentalapp.dto.ApartmentDto;
import com.bkravets.apartmentrentalapp.dto.BookingDto;
import com.bkravets.apartmentrentalapp.dto.UserDto;
import com.bkravets.apartmentrentalapp.dto.LoginDto;
import com.bkravets.apartmentrentalapp.entity.User;

import java.util.List;

public interface UserService {
    User getLoggedUser();

    UserDto getCurrentUser();

    UserDto create(UserDto userDTO);

    String login(LoginDto loginDTO);

    UserDto update(UserDto userDTO);

    List<ApartmentDto> getUserApartments();

    List<BookingDto> getUserBookings();
}

