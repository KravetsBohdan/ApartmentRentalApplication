package com.bkravets.apartmentrentalapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApartmentRentalAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApartmentRentalAppApplication.class, args);
    }

}
