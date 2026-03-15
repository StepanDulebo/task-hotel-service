package com.example.hotelservice.dto;

public  record AddressDto(
        String houseNumber,
        String street,
        String city,
        String country,
        String postCode
) {}