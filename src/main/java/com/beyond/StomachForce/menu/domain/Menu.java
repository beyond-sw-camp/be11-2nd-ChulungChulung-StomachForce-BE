package com.beyond.StomachForce.menu.domain;


import com.beyond.StomachForce.menu.dto.MenuResDto;
import com.beyond.StomachForce.restaurant.domain.Restaurant;
import com.beyond.StomachForce.reservation.domain.Reservation;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@ToString
@Entity
@AllArgsConstructor
@Builder
@Setter
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

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

    @Transient // 데이터베이스 컬럼에는 저장하지 않음
    private Integer quantity;


    public MenuResDto listFromEntity(){
        return MenuResDto.builder()
                .id(this.id)
                .name(this.name)
                .price(this.price)
                .description(this.description)
                .menuPhoto(this.menuPhoto)
                .quantity(this.quantity != null ? this.quantity : 1)
                .build();
    }

}
