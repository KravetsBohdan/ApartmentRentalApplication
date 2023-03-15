package com.bkravets.apartmentrentalapp.service.impl;

import com.bkravets.apartmentrentalapp.dto.ReviewDto;
import com.bkravets.apartmentrentalapp.entity.Booking;
import com.bkravets.apartmentrentalapp.entity.Review;
import com.bkravets.apartmentrentalapp.entity.Status;
import com.bkravets.apartmentrentalapp.entity.User;
import com.bkravets.apartmentrentalapp.exception.BadRequestException;
import com.bkravets.apartmentrentalapp.exception.ResourceNotFoundException;
import com.bkravets.apartmentrentalapp.mapper.ReviewMapper;
import com.bkravets.apartmentrentalapp.repository.BookingRepository;
import com.bkravets.apartmentrentalapp.repository.ReviewRepository;
import com.bkravets.apartmentrentalapp.service.ReviewService;
import com.bkravets.apartmentrentalapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;


    @Override
    public ReviewDto addReview(ReviewDto reviewDTO, long apartmentId) {
        Review review = ReviewMapper.INSTANCE.toReview(reviewDTO);
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
        User user = userService.getLoggedUser();

        Booking booking = bookingRepository.findByTenantIdAndApartmentId(user.getId(), apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() != Status.APPROVED) {
            throw new BadRequestException("You can't leave a review for an apartment you haven't stayed in");
        }

        review.setTenant(user);
        review.setApartment(booking.getApartment());
        review = reviewRepository.save(review);

        return ReviewMapper.INSTANCE.toReviewDTO(review);
    }

    @Override
    public ReviewDto updateReview(long reviewId, ReviewDto reviewDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User user = userService.getLoggedUser();

        if (!user.getEmail().equals(review.getTenant().getEmail())) {
            throw new BadRequestException("You are not allowed to perform this action");
        }

        review.setRating(reviewDTO.getRating());
        review.setReviewText(reviewDTO.getReviewText());
        review = reviewRepository.save(review);
        return ReviewMapper.INSTANCE.toReviewDTO(review);
    }

    @Override
    public void deleteReview(long reviewId) {
        User user = userService.getLoggedUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!user.getEmail().equals(review.getTenant().getEmail())) {
            throw new BadRequestException("You are not allowed to perform this action");
        }
        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewDto> getReviewsByApartmentId(long apartmentId) {
        List<Review> reviews = reviewRepository.findAllByApartmentId(apartmentId);
        return ReviewMapper.INSTANCE.toReviewDTOs(reviews);
    }
}
