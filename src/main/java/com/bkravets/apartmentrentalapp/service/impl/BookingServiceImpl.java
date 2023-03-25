package com.bkravets.apartmentrentalapp.service.impl;

import com.bkravets.apartmentrentalapp.dto.BookingDto;
import com.bkravets.apartmentrentalapp.entity.Apartment;
import com.bkravets.apartmentrentalapp.entity.Booking;
import com.bkravets.apartmentrentalapp.entity.Status;
import com.bkravets.apartmentrentalapp.entity.User;
import com.bkravets.apartmentrentalapp.exception.BadRequestException;
import com.bkravets.apartmentrentalapp.exception.ResourceNotFoundException;
import com.bkravets.apartmentrentalapp.mapper.BookingMapper;
import com.bkravets.apartmentrentalapp.repository.ApartmentRepository;
import com.bkravets.apartmentrentalapp.repository.BookingRepository;
import com.bkravets.apartmentrentalapp.service.BookingService;
import com.bkravets.apartmentrentalapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto addBooking(BookingDto bookingDTO, long apartmentId) {
        User tenant = userService.getLoggedUser();
        Apartment apartment = getApartmentById(apartmentId);

        validateBookingDates(bookingDTO, apartment);
        validateOwnerNotTenant(apartment, tenant);

        Booking bookingToAdd = BookingMapper.INSTANCE.toEntity(bookingDTO);
        bookingToAdd.setApartment(apartment);
        bookingToAdd.setTenant(tenant);
        bookingToAdd.setStatus(Status.PENDING);

        Booking savedBooking = bookingRepository.save(bookingToAdd);
        return BookingMapper.INSTANCE.toDTO(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto confirmBooking(long bookingId) {
        User owner = userService.getLoggedUser();
        Booking booking = getBookingByIdAndOwnerId(bookingId, owner.getId());

        booking.setStatus(Status.APPROVED);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.INSTANCE.toDTO(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto rejectBooking(long bookingId) {
        User owner = userService.getLoggedUser();
        Booking booking = getBookingByIdAndOwnerId(bookingId, owner.getId());

        booking.setStatus(Status.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.INSTANCE.toDTO(savedBooking);
    }

    @Override
    public void deleteBooking(long bookingId) {
        User tenant = userService.getLoggedUser();
        Booking bookingToDelete = getBookingById(bookingId);
        validateBookingOwnership(bookingToDelete, tenant);

        bookingRepository.delete(bookingToDelete);
    }

    @Override
    public List<BookingDto> getBookingsByApartmentId(long apartmentId) {
        List<Booking> bookings = bookingRepository.findAllByApartmentId(apartmentId);
        return BookingMapper.INSTANCE.toDTOs(bookings);
    }

    private Apartment getApartmentById(long apartmentId) {
        return apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found"));
    }

    private Booking getBookingById(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private Booking getBookingByIdAndOwnerId(long bookingId, long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getApartment().getOwner().getId() != ownerId) {
            throw new BadRequestException("You are not authorized to perform this action");
        }

        return booking;
    }

    private void validateBookingDates(BookingDto bookingDTO, Apartment apartment) {
        List<Booking> existingBookings = apartment.getBookings();

        boolean isDatesBooked = existingBookings.stream()
                .filter(booking -> booking.getStatus() == Status.PENDING || booking.getStatus() == Status.APPROVED)
                .anyMatch(booking -> booking.getStartDate().isBefore(bookingDTO.getEndDate()) &&
                        booking.getEndDate().isAfter(bookingDTO.getStartDate()));

        if (isDatesBooked) {
            throw new BadRequestException("Dates are already booked");
        }
    }

    private void validateOwnerNotTenant(Apartment apartment, User tenant) {
        if (apartment.getOwner().getId().equals(tenant.getId())) {
            throw new BadRequestException("Owner cannot book his own apartment");
        }
    }

    private void validateBookingOwnership(Booking booking, User tenant) {
        if (!booking.getTenant().getId().equals(tenant.getId())) {
            throw new BadRequestException("You are not authorized to perform this action");
        }
    }

    @Scheduled(cron = "@daily")
    public void cleanupBookings() {
        List<Booking> bookingsToClear = bookingRepository.findAllByEndDateBeforeOrStatus(LocalDate.now(), Status.REJECTED);

        bookingRepository.deleteAll(bookingsToClear);
    }
}