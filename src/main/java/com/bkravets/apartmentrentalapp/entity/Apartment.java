package com.bkravets.apartmentrentalapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "apartments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "apartment")
    private List<Review> reviews;
}
