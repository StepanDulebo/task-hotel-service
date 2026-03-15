package com.example.hotelservice.dto;

public record HotelShortDto(
        Long id,
        String name,
        String description,
        String address,
        String phone
) {}