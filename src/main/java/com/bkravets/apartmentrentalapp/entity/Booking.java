package com.bkravets.apartmentrentalapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Status status;

    @FutureOrPresent(message = "Start date should not be in the past")
    private LocalDate startDate;

    @FutureOrPresent(message = "Start date should not be in the past")
    private LocalDate endDate;

    @Min(value = 0, message = "Total price should be non negative")
    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    @JsonIgnore
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private User tenant;
}

