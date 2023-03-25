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
import org.apache.commons.lang3.StringUtils;
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
    @Transactional(readOnly = true)
    public Page<ApartmentDto> getAllApartments(int page,
                                               int size,
                                               String city,
                                               String query,
                                               String sortBy,
                                               String sortDir) {

        Sort sort = Sort.by(sortBy);
        if (sortDir != null && sortDir.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Apartment> apartmentsPage;

        if (StringUtils.isEmpty(city) && StringUtils.isEmpty(query)) {
            apartmentsPage = apartmentRepository.findAll(pageable);
        } else if (StringUtils.isEmpty(city) || city.equalsIgnoreCase("All cities")) {
            apartmentsPage = apartmentRepository.findByTitleContaining(query, pageable);
        } else if (StringUtils.isEmpty(query)) {
            apartmentsPage = apartmentRepository.findByCity(city, pageable);
        } else {
            apartmentsPage = apartmentRepository.findByCityAndTitleContaining(city, query, pageable);
        }

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
