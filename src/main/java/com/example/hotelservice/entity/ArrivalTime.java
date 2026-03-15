package com.example.hotelservice.entity;
import jakarta.persistence.Embeddable;

@Embeddable
public record ArrivalTime(
        String checkIn,
        String checkOut
) {}