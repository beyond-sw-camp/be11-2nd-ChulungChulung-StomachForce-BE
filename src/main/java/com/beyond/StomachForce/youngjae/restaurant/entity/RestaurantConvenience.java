package com.beyond.StomachForce.youngjae.restaurant.entity;

import com.beyond.StomachForce.youngjae.common.entity.BaseTimeEntity;
import com.beyond.StomachForce.youngjae.restaurant.entity.select.Convenience;
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
public class RestaurantConvenience extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Convenience convenience;        //편의기능 분류
    private String name;                    //편의기능 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
