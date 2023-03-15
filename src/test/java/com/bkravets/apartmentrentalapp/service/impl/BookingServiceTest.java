package com.bkravets.apartmentrentalapp.service.impl;

import com.bkravets.apartmentrentalapp.dto.BookingDto;
import com.bkravets.apartmentrentalapp.entity.Apartment;
import com.bkravets.apartmentrentalapp.entity.Booking;
import com.bkravets.apartmentrentalapp.entity.Status;
import com.bkravets.apartmentrentalapp.entity.User;
import com.bkravets.apartmentrentalapp.exception.BadRequestException;
import com.bkravets.apartmentrentalapp.exception.ResourceNotFoundException;
import com.bkravets.apartmentrentalapp.repository.ApartmentRepository;
import com.bkravets.apartmentrentalapp.repository.BookingRepository;
import com.bkravets.apartmentrentalapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ApartmentRepository apartmentRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User validUser;
    private User user;
    private Apartment validApartment;
    private Booking validBooking;
    private BookingDto bookingDto;

    @BeforeEach
    public void setUp() {
        validApartment = new Apartment(1L, "Apartment 1", "Description", "Lviv", "Here", 4, 200,"url", null, new ArrayList<>(), new ArrayList<>());
        validUser = new User(1L, "john@mail.com", "+38", "john", "doe", "123456",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        validApartment.setOwner(validUser);
        validUser.setApartments(List.of(validApartment));
        validBooking = new Booking(1L, Status.PENDING, LocalDate.now(), LocalDate.now().plusDays(5), 1000, validApartment, validUser);
        bookingDto = new BookingDto(1L,"PENDING", LocalDate.now(), LocalDate.now().plusDays(5), 1000, validApartment.getCity(), validApartment.getLocation());
        user = new User(2L, "den@mail.com", "+38", "den", "doe", "123456",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Test
    void addBooking_shouldCreateNewBooking() {
        // Given
        when(userService.getLoggedUser()).thenReturn(user);
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.of(validApartment));
        when(bookingRepository.save(any(Booking.class))).thenReturn(validBooking);

        // When
        BookingDto savedBookingDto = bookingService.addBooking(bookingDto, 1L);

        // Then
        verify(apartmentRepository).findById(validApartment.getId());
        verify(bookingRepository).save(any(Booking.class));
        assertThat(savedBookingDto).isEqualTo(bookingDto);

    }


    @Test
    void addBooking_shouldThrowExceptionWhenOwnerIsTenant() {
        // Given
        when(userService.getLoggedUser()).thenReturn(validUser);
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.of(validApartment));

        // When & Then
        assertThatThrownBy(() -> bookingService.addBooking(bookingDto, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Owner cannot book his own apartment");

        verify(apartmentRepository).findById(validApartment.getId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    @Test
    void addBooking_shouldThrowExceptionWhenInvalidApartmentId() {
        // Given
        when(userService.getLoggedUser()).thenReturn(validUser);
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookingService.addBooking(bookingDto, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Apartment not found");
        verify(apartmentRepository).findById(anyLong());
        verify(bookingRepository, never()).save(validBooking);
    }

    @Test
    void addBooking_shouldThrowExceptionWhenBookingDatesAlreadyBooked() {
        // Given
        when(userService.getLoggedUser()).thenReturn(user);
        when(apartmentRepository.findById(validApartment.getId())).thenReturn(Optional.of(validApartment));
        validApartment.setBookings(List.of(new Booking(2L, Status.APPROVED, LocalDate.now().plusDays(2), LocalDate.now().plusDays(6), 1200, validApartment, validUser)));

        // When & Then
        assertThatThrownBy(() -> bookingService.addBooking(bookingDto, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Dates are already booked");
    }


    @Test
    void confirmBooking_shouldReturnConfirmedBooking_whenBookingExistsAndUserIsOwner() {
        // Given
        when(userService.getLoggedUser()).thenReturn(validUser);
        when(bookingRepository.findById(validBooking.getId())).thenReturn(Optional.of(validBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(validBooking);

        // When
        BookingDto confirmedBooking = bookingService.confirmBooking(validBooking.getId());

        // Then
        assertThat(confirmedBooking).isNotNull();
        assertThat(confirmedBooking.getStatus()).isEqualTo(Status.APPROVED.name());
    }

    @Test
    void confirmBooking_shouldThrowResourceNotFoundException_whenBookingDoesNotExist() {
        // Given
        when(userService.getLoggedUser()).thenReturn(validUser);
        when(bookingRepository.findById(validBooking.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookingService.confirmBooking(validBooking.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Booking not found");
    }

    @Test
    void confirmBooking_shouldThrowBadRequestException_whenUserIsNotOwnerOfBooking() {
        // Given
        User anotherUser = new User(2L, "jane@mail.com", "+38", "jane", "doe", "123456",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(userService.getLoggedUser()).thenReturn(anotherUser);
        when(bookingRepository.findById(validBooking.getId())).thenReturn(Optional.of(validBooking));

        // When & Then
        assertThatThrownBy(() -> bookingService.confirmBooking(validBooking.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("You are not authorized to perform this action");
    }

    @Test
    void rejectBooking_shouldReturnRejectedBooking_whenBookingExistsAndUserIsOwner() {
        // Given
        when(userService.getLoggedUser()).thenReturn(validUser);
        when(bookingRepository.findById(validBooking.getId())).thenReturn(Optional.of(validBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(validBooking);

        // When
        BookingDto rejectedBooking = bookingService.rejectBooking(validBooking.getId());

        // Then
        assertThat(rejectedBooking).isNotNull();
        assertThat(rejectedBooking.getStatus()).isEqualTo(Status.REJECTED.name());
    }

    @Test
    void rejectBooking_shouldThrowResourceNotFoundException_whenBookingDoesNotExist() {
        // Given
        when(userService.getLoggedUser()).thenReturn(validUser);
        when(bookingRepository.findById(validBooking.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookingService.rejectBooking(validBooking.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Booking not found");
    }

    @Test
    void rejectBooking_shouldThrowBadRequestException_whenUserIsNotOwnerOfBooking() {
        // Given
        User anotherUser = new User(2L, "jane@mail.com", "+38", "jane", "doe", "123456",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(userService.getLoggedUser()).thenReturn(anotherUser);
        when(bookingRepository.findById(validBooking.getId())).thenReturn(Optional.of(validBooking));

        // When & Then
        assertThatThrownBy(() -> bookingService.rejectBooking(validBooking.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("You are not authorized to perform this action");
    }

}