package com.example.hotelservice.specification;

import com.example.hotelservice.entity.Amenity;
import com.example.hotelservice.entity.Hotel;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class HotelSpecifications {

    public static Specification<Hotel> searchHotels(
            String name,
            String brand,
            String city,
            String country,
            List<String> amenities) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase().trim() + "%"));
            }

            if (brand != null && !brand.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("brand"), brand.trim()));
            }

            if (city != null && !city.trim().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("address").get("city"),
                        city.trim()
                ));
            }

            if (country != null && !country.trim().isEmpty()) {
                predicates.add(cb.equal(
                        root.get("address").get("country"),
                        country.trim()
                ));
            }

            if (amenities != null && !amenities.isEmpty()) {
                Join<Hotel, Amenity> join = root.join("amenities", JoinType.LEFT);


                for (String amenityName : amenities) {
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<Hotel> subRoot = subquery.correlate(root);
                    Join<Hotel, Amenity> subJoin = subRoot.join("amenities");
                    subquery.select(cb.literal(1L))
                            .where(cb.equal(subJoin.get("name"), amenityName.trim()));
                    predicates.add(cb.exists(subquery));
                }
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}