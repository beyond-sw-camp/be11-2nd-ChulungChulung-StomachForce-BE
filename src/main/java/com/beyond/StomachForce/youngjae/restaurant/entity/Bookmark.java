package com.beyond.StomachForce.youngjae.restaurant.entity;

import com.beyond.StomachForce.youngjae.common.entity.BaseTimeEntity;
import com.beyond.StomachForce.youngjae.restaurant.entity.select.BookmarkType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter

public class Bookmark extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)          //     레스토랑 테이블과 연결
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

//    @ManyToOne(fetch = FetchType.LAZY)        //      customer이랑 연결 짓는 것
//    @JoinColumn(name = "customer_id", nullable = false)
//    private Customer customer;

    @Enumerated(EnumType.STRING)
    private BookmarkType bookmarkType;


}
