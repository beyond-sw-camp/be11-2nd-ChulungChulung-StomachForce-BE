package com.beyond.StomachForce.menu.domain;


import com.beyond.StomachForce.allergyInfo.domain.AllergyInfo;
import com.beyond.StomachForce.menu.dto.MenuResDto;
import com.beyond.StomachForce.reservation.domain.Reservation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@Entity
@AllArgsConstructor
@Builder

public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "restaurant_id")
//    private Restaurant restaurant;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private Long price;

    @Column(length = 3000, nullable = false)
    private String description;

    @Column// 기본 메뉴 이미지 url삽입
    private String menuPhoto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "allergyInfo_id")
    private AllergyInfo allergyInfo;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    public MenuResDto listFromEntity(){
        return MenuResDto.builder()
                .id(this.id)
                .price(this.price)
                .description(this.description)
                .menuPhoto(this.menuPhoto)
                .build();
    }
}
