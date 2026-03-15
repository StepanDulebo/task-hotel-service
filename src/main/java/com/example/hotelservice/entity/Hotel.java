package com.example.hotelservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hotel")
@Getter
@Setter
@NoArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String brand;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "houseNumber", column = @Column(name = "address_house_number")),
            @AttributeOverride(name = "street",      column = @Column(name = "address_street")),
            @AttributeOverride(name = "city",        column = @Column(name = "address_city")),
            @AttributeOverride(name = "country",     column = @Column(name = "address_country")),
            @AttributeOverride(name = "postCode",    column = @Column(name = "address_post_code"))
    })
    private Address address;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "phone", column = @Column(name = "contacts_phone")),
            @AttributeOverride(name = "email", column = @Column(name = "contacts_email"))
    })
    private Contacts contacts;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "checkIn",  column = @Column(name = "arrival_time_check_in")),
            @AttributeOverride(name = "checkOut", column = @Column(name = "arrival_time_check_out"))
    })
    private ArrivalTime arrivalTime;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "hotel_amenity",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();
}