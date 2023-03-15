package com.bkravets.apartmentrentalapp.service.impl;

import com.bkravets.apartmentrentalapp.dto.ApartmentDto;
import com.bkravets.apartmentrentalapp.entity.Apartment;
import com.bkravets.apartmentrentalapp.entity.User;
import com.bkravets.apartmentrentalapp.exception.ResourceNotFoundException;
import com.bkravets.apartmentrentalapp.repository.ApartmentRepository;
import com.bkravets.apartmentrentalapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceImplTest {

    @Mock
    private ApartmentRepository apartmentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ApartmentServiceImpl apartmentService;

    private Apartment apartment;
    private ApartmentDto apartmentDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "new@email.com", "+38", "ren", "dou", "encriptedPassword", new ArrayList<>(), null, null);
        apartment = new Apartment(1L, "Title 1", "Description 1", "City 1", "Location 1", 1, 1, "url", null, null, null);
        apartmentDto = new ApartmentDto(1L, "Title 1", "Description 1", "City 1", "Location 1", 1, 1, "url");
        user.setApartments(List.of(apartment));
        apartment.setOwner(user);
    }

    @Test
    void getApartment_shouldReturnApartmentDto() {
        // Given
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.of(apartment));

        // When
        ApartmentDto actualApartmentDto = apartmentService.getApartment(1L);

        // Then
        assertThat(actualApartmentDto).isEqualTo(apartmentDto);
        verify(apartmentRepository).findById(anyLong());
    }

    @Test
    void getApartment_shouldThrowExceptionWhenApartmentNotFound() {
        // Given
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> apartmentService.getApartment(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Apartment not found");
    }


    @Test
    void addApartment_shouldCreateNewApartment() {
        // Given
        when(userService.getLoggedUser()).thenReturn(user);
        when(apartmentRepository.save(any(Apartment.class))).thenReturn(apartment);

        // When
        ApartmentDto actualApartmentDto = apartmentService.addApartment(apartmentDto);

        // Then
        assertThat(actualApartmentDto).isEqualTo(apartmentDto);
    }


    @Test
    void updateApartment_shouldUpdateExistingApartment() {
        // Given
        ApartmentDto updatedApartmentDto = new ApartmentDto(1L, "New Title", "New Description", "New City", "New Location", 2, 2, "url");
        Apartment updatedApartment = new Apartment(1L, "New Title", "New Description", "New City", "New Location", 2, 2.0,"url", null, null, null);
        when(userService.getLoggedUser()).thenReturn(user);
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.of(apartment));
        when(apartmentRepository.save(any(Apartment.class))).thenReturn(updatedApartment);

        // When
        ApartmentDto actualApartmentDto = apartmentService.updateApartment(1L, updatedApartmentDto);

        // Then
        assertThat(actualApartmentDto).isEqualTo(updatedApartmentDto);

        verify(apartmentRepository).findById(1L);
        verify(apartmentRepository).save(apartment);
    }

    @Test
    void updateApartment_shouldThrowExceptionWhenApartmentNotFound() {
        // Given
        ApartmentDto updatedApartmentDto = new ApartmentDto(1L, "New Title", "New Description", "New City", "New Location", 2, 2,"url");
        when(userService.getLoggedUser()).thenReturn(user);
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.empty());


        // When & Then
        assertThatThrownBy(() -> apartmentService.updateApartment(1L, updatedApartmentDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Apartment not found");

        verify(apartmentRepository).findById(1L);
        verify(apartmentRepository, never()).save(apartment);
    }

    @Test
    void updateApartment_shouldThrowExceptionWhenUserNotOwner() {
        // Given
        ApartmentDto updatedApartmentDto = new ApartmentDto(1L, "New Title", "New Description", "New City", "New Location", 2, 2, "url");
        when(userService.getLoggedUser()).thenReturn(null);
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.of(apartment));


        // When & Then
        assertThatThrownBy(() -> apartmentService.updateApartment(1L, updatedApartmentDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("You are not the owner of this apartment");

        verify(apartmentRepository).findById(1L);
        verify(apartmentRepository, never()).save(apartment);
    }

    @Test
    void deleteApartment_shouldDeleteApartment() {
        // Given
        when(userService.getLoggedUser()).thenReturn(user);
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.of(apartment));

        // When
        apartmentService.deleteApartment(1L);

        // Then
        verify(apartmentRepository).findById(1L);
        verify(apartmentRepository).delete(apartment);
    }

    @Test
    void deleteApartment_shouldThrowExceptionWhenApartmentNotFound() {
        // Given
        when(userService.getLoggedUser()).thenReturn(user);
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> apartmentService.deleteApartment(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Apartment not found");

        verify(apartmentRepository).findById(1L);
        verify(apartmentRepository, never()).delete(apartment);
    }

    @Test
    void deleteApartment_shouldThrowExceptionWhenUserNotOwner() {
        // Given
        when(userService.getLoggedUser()).thenReturn(null);
        when(apartmentRepository.findById(anyLong())).thenReturn(Optional.of(apartment));

        // When & Then
        assertThatThrownBy(() -> apartmentService.deleteApartment(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("You are not the owner of this apartment");

        verify(apartmentRepository).findById(1L);
        verify(apartmentRepository, never()).delete(apartment);
    }

}