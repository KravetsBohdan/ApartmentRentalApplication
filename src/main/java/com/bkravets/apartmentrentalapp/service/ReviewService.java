package com.bkravets.apartmentrentalapp.service;

import com.bkravets.apartmentrentalapp.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addReview(ReviewDto reviewDTO, long apartmentId);

    ReviewDto updateReview(long reviewId, ReviewDto reviewDTO);

    void deleteReview(long reviewId);

    List<ReviewDto> getReviewsByApartmentId(long apartmentId);
}
