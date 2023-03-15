package com.bkravets.apartmentrentalapp.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long id;

    private String status;

    @FutureOrPresent(message = "Start date should not be in the past")
    private LocalDate startDate;

    @FutureOrPresent(message = "Start date should not be in the past")
    private LocalDate endDate;

    @Min(value = 0, message = "Total price should be non negative")
    private double totalPrice;

    private String city;
    private String location;
}
