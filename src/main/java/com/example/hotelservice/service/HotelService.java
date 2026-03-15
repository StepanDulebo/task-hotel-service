package com.example.hotelservice.service;

import com.example.hotelservice.dto.*;

import com.example.hotelservice.entity.*;
import com.example.hotelservice.exception.HotelNotFoundException;
import com.example.hotelservice.repository.AmenityRepository;
import com.example.hotelservice.repository.HotelRepository;
import com.example.hotelservice.specification.HotelSpecifications;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final JdbcTemplate jdbcTemplate;

    public List<HotelShortDto> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(h -> new HotelShortDto(
                        h.getId(),
                        h.getName(),
                        h.getDescription(),
                        String.format("%s %s, %s, %s, %s",
                                h.getAddress().houseNumber(),
                                h.getAddress().street(),
                                h.getAddress().city(),
                                h.getAddress().postCode(),
                                h.getAddress().country()),
                        h.getContacts().phone()
                ))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public HotelDetailDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));

        List<String> amenityNames = hotel.getAmenities().stream()
                .map(Amenity::getName)
                .sorted()
                .collect(Collectors.toList());

        return new HotelDetailDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                hotel.getBrand(),
                new AddressDto(
                        hotel.getAddress().houseNumber(),
                        hotel.getAddress().street(),
                        hotel.getAddress().city(),
                        hotel.getAddress().country(),
                        hotel.getAddress().postCode()
                ),
                new ContactsDto(
                        hotel.getContacts().phone(),
                        hotel.getContacts().email()
                ),
                new ArrivalTimeDto(
                        hotel.getArrivalTime() != null ? hotel.getArrivalTime().checkIn() : null,
                        hotel.getArrivalTime() != null ? hotel.getArrivalTime().checkOut() : null
                ),
                amenityNames
        );
    }


    @Transactional
    public List<String> addAmenities(Long hotelId, List<String> amenityNames) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));

                List<Amenity> amenitiesToAdd = amenityNames.stream()
                .distinct()
                .map(name -> amenityRepository.findByName(name.trim())
                        .orElseGet(() -> {
                            Amenity newAmenity = new Amenity(name.trim());
                            return amenityRepository.save(newAmenity);
                        }))
                .collect(Collectors.toList());


        Set<Amenity> current = hotel.getAmenities();
        amenitiesToAdd.forEach(amenity -> {
            if (!current.contains(amenity)) {
                current.add(amenity);
            }
        });

        hotelRepository.save(hotel);

        return current.stream()
                .map(Amenity::getName)
                .sorted()
                .collect(Collectors.toList());
    }


    @Transactional
    public HotelShortDto createHotel(HotelCreateDto dto) {
        Hotel hotel = new Hotel();
        hotel.setName(dto.name());
        hotel.setDescription(dto.description());
        hotel.setBrand(dto.brand());


        Address address = new Address(
                dto.address().houseNumber(),
                dto.address().street(),
                dto.address().city(),
                dto.address().country(),
                dto.address().postCode()
        );
        hotel.setAddress(address);

        Contacts contacts = new Contacts(
                dto.contacts().phone(),
                dto.contacts().email()
        );
        hotel.setContacts(contacts);

        if (dto.arrivalTime() != null) {
            ArrivalTime arrivalTime = new ArrivalTime(
                    dto.arrivalTime().checkIn(),
                    dto.arrivalTime().checkOut()
            );
            hotel.setArrivalTime(arrivalTime);
        }


        Hotel saved = hotelRepository.save(hotel);

        String fullAddress = String.format("%s %s, %s, %s, %s",
                saved.getAddress().houseNumber(),
                saved.getAddress().street(),
                saved.getAddress().city(),
                saved.getAddress().postCode(),
                saved.getAddress().country()
        );

        return new HotelShortDto(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                fullAddress,
                saved.getContacts().phone()
        );
    }




    public List<HotelShortDto> searchHotels(
            String name,
            String brand,
            String city,
            String country,
            List<String> amenities) {

        Specification<Hotel> spec = HotelSpecifications.searchHotels(name, brand, city, country, amenities);

        return hotelRepository.findAll(spec).stream()
                .map(h -> new HotelShortDto(
                        h.getId(),
                        h.getName(),
                        h.getDescription(),
                        String.format("%s %s, %s, %s, %s",
                                h.getAddress().houseNumber(),
                                h.getAddress().street(),
                                h.getAddress().city(),
                                h.getAddress().postCode(),
                                h.getAddress().country()),
                        h.getContacts() != null ? h.getContacts().phone() : null
                ))
                .collect(Collectors.toList());
    }



    public Map<String, Long> getHistogram(String param) {
        String sql;

        switch (param.toLowerCase()) {
            case "brand":
                sql = "SELECT brand AS \"value\", COUNT(*) AS \"count\" " +
                        "FROM hotel " +
                        "WHERE brand IS NOT NULL " +
                        "GROUP BY brand " +
                        "ORDER BY \"count\" DESC, \"value\"";
                break;

            case "city":
                sql = "SELECT address_city AS \"value\", COUNT(*) AS \"count\" " +
                        "FROM hotel " +
                        "WHERE address_city IS NOT NULL " +
                        "GROUP BY address_city " +
                        "ORDER BY \"count\" DESC, \"value\"";
                break;

            case "country":
                sql = "SELECT address_country AS \"value\", COUNT(*) AS \"count\" " +
                        "FROM hotel " +
                        "WHERE address_country IS NOT NULL " +
                        "GROUP BY address_country " +
                        "ORDER BY \"count\" DESC, \"value\"";
                break;

            case "amenities":
                sql = "SELECT a.name AS \"value\", COUNT(DISTINCT ha.hotel_id) AS \"count\" " +
                        "FROM amenity a " +
                        "LEFT JOIN hotel_amenity ha ON a.id = ha.amenity_id " +
                        "GROUP BY a.name " +
                        "ORDER BY \"count\" DESC, \"value\"";
                break;

            default:
                throw new IllegalArgumentException("Unsupported parameter: " + param +
                        ". Allowed: brand, city, country, amenities");
        }

         List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        Map<String, Long> result = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String key = (String) row.get("value");
            Number countNum = (Number) row.get("count");
            long count = countNum != null ? countNum.longValue() : 0;

            if (key != null && !key.trim().isEmpty()) {
                result.put(key, count);
            }
        }

        return result;
    }

}