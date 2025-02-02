package com.beyond.StomachForce.youngjae.restaurant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public  class RestaurantAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;       // 도시명
    private String street;     // 거리명
    private String zipCode;    // 우편번호

    @OneToOne(mappedBy = "address")
    private Restaurant restaurant;

    public String getFullAddress() {
        return city + " " + street + " " + zipCode;
    }

}
