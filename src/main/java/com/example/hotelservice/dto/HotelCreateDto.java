package com.example.hotelservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HotelCreateDto(
        @NotBlank(message = "Name is required")
        String name,

        String description,

        String brand,

        @NotNull(message = "Address is required")
        @Valid
        AddressDto address,

        @NotNull(message = "Contacts are required")
        @Valid
        ContactsDto contacts,

        @Valid
        ArrivalTimeDto arrivalTime   // может быть null — optional
) {}