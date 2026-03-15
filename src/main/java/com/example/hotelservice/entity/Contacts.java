package com.example.hotelservice.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record Contacts(
        String phone,
        String email
) {}