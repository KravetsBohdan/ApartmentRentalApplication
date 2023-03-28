package com.bkravets.apartmentrentalapp.service.impl;

import com.bkravets.apartmentrentalapp.dto.ReviewDto;
import com.bkravets.apartmentrentalapp.entity.Apartment;
import com.bkravets.apartmentrentalapp.entity.Review;
import com.bkravets.apartmentrentalapp.entity.User;
import com.bkravets.apartmentrentalapp.exception.AuthorizationException;
import com.bkravets.apartmentrentalapp.exception.BadRequestException;
import com.bkravets.apartmentrentalapp.exception.ResourceNotFoundException;
import com.bkravets.apartmentrentalapp.mapper.ReviewMapper;
import com.bkravets.apartmentrentalapp.repository.ApartmentRepository;
import com.bkravets.apartmentrentalapp.repository.ReviewRepository;
import com.bkravets.apartmentrentalapp.service.ReviewService;
import com.bkravets.apartmentrentalapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserService userService;


    @Override
    @Transactional
    public ReviewDto addReview(ReviewDto reviewDTO, long apartmentId) {
        Review review = ReviewMapper.INSTANCE.toReview(reviewDTO);

        User user = userService.getLoggedUser();
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found"));

        validateOwnerNotReviewAuthor(apartment, user);

        review.setTenant(user);
        review.setApartment(apartment);
        review = reviewRepository.save(review);

        return ReviewMapper.INSTANCE.toReviewDTO(review);
    }

    @Override
    public ReviewDto updateReview(long reviewId, ReviewDto reviewDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User user = userService.getLoggedUser();

        validateReviewOwnership(user, review);

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

        validateReviewOwnership(user, review);
        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewDto> getReviewsByApartmentId(long apartmentId) {
        List<Review> reviews = reviewRepository.findAllByApartmentId(apartmentId);
        return ReviewMapper.INSTANCE.toReviewDTOs(reviews);
    }

    private void validateOwnerNotReviewAuthor(Apartment apartment, User user) {
        if (apartment.getOwner().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot review your own apartment");
        }
    }

    private void validateReviewOwnership(User user, Review review) {
        if (!user.getId().equals(review.getTenant().getId())) {
            throw new AuthorizationException("You are not allowed to perform this action");
        }
    }
}
