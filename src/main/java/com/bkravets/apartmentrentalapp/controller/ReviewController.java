package com.bkravets.apartmentrentalapp.controller;

import com.bkravets.apartmentrentalapp.dto.ReviewDto;
import com.bkravets.apartmentrentalapp.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;



    @PostMapping("/{apartmentId}")
    public ResponseEntity<ReviewDto> addReview(@Valid @RequestBody ReviewDto reviewDTO,
                                               @RequestParam long apartmentId) {
        ReviewDto createdReview = reviewService.addReview(reviewDTO, apartmentId);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable long reviewId,
                                                  @Valid @RequestBody ReviewDto reviewDTO) {
        ReviewDto updatedReview = reviewService.updateReview(reviewId, reviewDTO);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable long reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/apartment/{apartmentId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByApartmentId(@PathVariable long apartmentId) {
        List<ReviewDto> reviews = reviewService.getReviewsByApartmentId(apartmentId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}
