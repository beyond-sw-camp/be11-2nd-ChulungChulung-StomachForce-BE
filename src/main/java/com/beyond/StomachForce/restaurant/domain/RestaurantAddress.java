package com.beyond.StomachForce.restaurant.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public  class RestaurantAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;       // 도시명
    private String street;     // 거리명


    @OneToOne(mappedBy = "address")
    private Restaurant restaurant;

    public String getFullAddress() {
        return city + " " + street ;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setStreet(String street) {
        this.street = street;
    }

}
