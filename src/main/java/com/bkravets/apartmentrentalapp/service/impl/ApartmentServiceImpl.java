package com.bkravets.apartmentrentalapp.service.impl;

import com.bkravets.apartmentrentalapp.dto.ApartmentDto;
import com.bkravets.apartmentrentalapp.dto.BookingDto;
import com.bkravets.apartmentrentalapp.entity.Apartment;
import com.bkravets.apartmentrentalapp.entity.User;
import com.bkravets.apartmentrentalapp.exception.AuthorizationException;
import com.bkravets.apartmentrentalapp.exception.BadRequestException;
import com.bkravets.apartmentrentalapp.exception.ResourceNotFoundException;
import com.bkravets.apartmentrentalapp.mapper.ApartmentMapper;
import com.bkravets.apartmentrentalapp.mapper.BookingMapper;
import com.bkravets.apartmentrentalapp.repository.ApartmentRepository;
import com.bkravets.apartmentrentalapp.service.ApartmentService;
import com.bkravets.apartmentrentalapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ApartmentServiceImpl implements ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final UserService userService;

    @Override
    public ApartmentDto getApartment(long id) {
        Apartment apartment = getApartmentById(id);
        return ApartmentMapper.INSTANCE.toDTO(apartment);
    }

    @Override
    @Transactional
    public ApartmentDto addApartment(ApartmentDto apartmentDTO) {
        User user = userService.getLoggedUser();
        Apartment apartment = ApartmentMapper.INSTANCE.toEntity(apartmentDTO);

        apartment.setOwner(user);

        apartment = apartmentRepository.save(apartment);

        return ApartmentMapper.INSTANCE.toDTO(apartment);
    }

    @Override
    @Transactional
    public ApartmentDto updateApartment(long apartmentId, ApartmentDto apartmentDTO) {
        User user = userService.getLoggedUser();

        Apartment apartment = getApartmentById(apartmentId);

        checkIfUserIsOwner(apartment, user);

        apartment.setTitle(apartmentDTO.getTitle());
        apartment.setCity(apartmentDTO.getCity().toUpperCase());
        apartment.setDescription(apartmentDTO.getDescription());
        apartment.setPricePerDay(apartmentDTO.getPricePerDay());
        apartment.setLocation(apartmentDTO.getLocation());
        apartment.setRoomsNumber(apartmentDTO.getRoomsNumber());

        apartment = apartmentRepository.save(apartment);
        return ApartmentMapper.INSTANCE.toDTO(apartment);
    }

    @Override
    @Transactional
    public void deleteApartment(long apartmentId) {
        User user = userService.getLoggedUser();
        Apartment apartment = getApartmentById(apartmentId);
        checkIfUserIsOwner(apartment, user);

        if (!apartment.getBookings().isEmpty()) {
            throw new BadRequestException("You can't delete apartment with bookings");
        }

        apartmentRepository.delete(apartment);
    }



    @Override
    @Transactional
    public List<LocalDate> getBookedDaysByApartmentId(long id) {
        Apartment apartment = getApartmentById(id);
        return apartment.getBookings().stream()
                .flatMap(booking -> booking.getStartDate()
                        .datesUntil(booking.getEndDate().plusDays(1)))
                .toList();
    }


    @Override
    @Transactional
    public Page<ApartmentDto> getAllApartments(String query,
                                               String city,
                                               int page,
                                               int size,
                                               String sortBy,
                                               String sortDir) {
        Sort sort = Sort.Direction.ASC.name().equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Apartment> apartmentsPage = apartmentRepository.findAllByTitleContainingAndCityContaining(query,city, pageable);

        return apartmentsPage.map(ApartmentMapper.INSTANCE::toDTO);
    }

    @Override
    @Transactional
    public List<BookingDto> getBookingsByApartmentId(long apartmentId) {
        return getApartmentById(apartmentId)
                .getBookings()
                .stream()
                .map(BookingMapper.INSTANCE::toDTO)
                .toList();
    }


    private void checkIfUserIsOwner(Apartment apartment, User user) {
        if (!apartment.getOwner().equals(user)) {
            throw new AuthorizationException("You are not the owner of this apartment");
        }
    }

    private Apartment getApartmentById(long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found"));
    }
}
