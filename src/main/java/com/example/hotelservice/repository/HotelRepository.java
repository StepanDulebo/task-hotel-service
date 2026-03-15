package com.example.hotelservice.repository;

import com.example.hotelservice.entity.Hotel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findAll(Specification<Hotel> spec);

}