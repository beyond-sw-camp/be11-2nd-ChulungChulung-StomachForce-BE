package com.beyond.StomachForce.restaurant.domain;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.restaurant.domain.select.RestaurantInfoStatus;
import com.beyond.StomachForce.restaurant.dtos.forRestaurantInfo.RestaurantInfoListRes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Builder
public class RestaurantInfo extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String informationText;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RestaurantInfoStatus restaurantInfoStatus = RestaurantInfoStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public RestaurantInfoListRes restaurantInfoListRes(){
        return RestaurantInfoListRes.builder()
                .informationText(this.informationText)
                .build();
    }


    //      생성할 때 쓰는 메서드
    public void updateInfo(String infoTest) {
        if (infoTest != null && !infoTest.isBlank()) {
            this.informationText = infoTest;
        }
    }


    //      지울 때 쓰는 메서드
    public void deactivate() {
        this.restaurantInfoStatus = RestaurantInfoStatus.INACTIVE;
    }

    //      최상단 5개는 항상 활성화해야하므로 필요한 메서드 디엑티브->액티브로 바꿔줌
    public void activate() {
        this.restaurantInfoStatus = RestaurantInfoStatus.ACTIVE;
    }

}
