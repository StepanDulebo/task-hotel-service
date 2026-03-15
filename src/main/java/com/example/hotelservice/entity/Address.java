package com.example.hotelservice.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record Address(
        String houseNumber,
        String street,
        String city,
        String country,
        String postCode
) {}