package com.bkravets.apartmentrentalapp.mapper;

import com.bkravets.apartmentrentalapp.dto.ReviewDto;
import com.bkravets.apartmentrentalapp.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    ReviewDto toReviewDTO(Review review);
    Review toReview(ReviewDto reviewDTO);

    List<ReviewDto> toReviewDTOs(List<Review> reviews);
}
