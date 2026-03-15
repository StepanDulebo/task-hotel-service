package com.example.hotelservice.controller;

import com.example.hotelservice.dto.*;
import com.example.hotelservice.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/property-view")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping("/hotels")
    public List<HotelShortDto> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/hotels/{id}")
    public HotelDetailDto getHotelById(@PathVariable Long id) {
        return hotelService.getHotelById(id);
    }

    @PostMapping("/hotels")
    @ResponseStatus(HttpStatus.CREATED)
    public HotelShortDto createHotel(@Valid @RequestBody HotelCreateDto dto) {
        return hotelService.createHotel(dto);
    }



    @PostMapping("/hotels/{id}/amenities")
    public ResponseEntity<List<String>> addAmenitiesToHotel(
            @PathVariable Long id,
            @RequestBody List<String> amenities) {   // ← ИЗМЕНЕНО

        List<String> updatedAmenities = hotelService.addAmenities(id, amenities); // ← убрали .amenities()
        return ResponseEntity.ok(updatedAmenities);
    }

    @GetMapping("/search")
    public List<HotelShortDto> searchHotels(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<String> amenities) {

        return hotelService.searchHotels(name, brand, city, country, amenities);
    }


    @GetMapping("/histogram/{param}")
    public Map<String, Long> getHistogram(@PathVariable String param) {
        return hotelService.getHistogram(param);
    }



}

