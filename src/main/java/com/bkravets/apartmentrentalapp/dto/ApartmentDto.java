package com.bkravets.apartmentrentalapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
public class ApartmentDto {
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotBlank(message = "City is mandatory")
    private String city;

    @NotBlank(message = "Location is mandatory")
    private String location;

    @Min(value = 1, message = "Number of rooms must be at least 1")
    private int roomsNumber;

    @Min(value = 0, message = "Price should be non negative")
    private double pricePerDay;
    private String photoUrl;
}

