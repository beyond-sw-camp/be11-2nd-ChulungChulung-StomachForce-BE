package com.beyond.StomachForce.restaurant.domain;


import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.menu.domain.Menu;
import com.beyond.StomachForce.restaurant.domain.select.*;
import com.beyond.StomachForce.restaurant.dtos.RestaurantDetailRes;
import com.beyond.StomachForce.restaurant.dtos.RestaurantListRes;
import com.beyond.StomachForce.restaurant.dtos.RestaurantUpdateReq;
import com.beyond.StomachForce.review.entity.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
public class Restaurant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                             // 고유id

    @Column(unique = true, nullable = false)
    private String name;                         // 레스토랑 아름

    @Column(unique = true, nullable = false)
    private String registrationNumber;           // 사업자등록번호

    @Column(nullable = false)                    // 비밀번호
    private String password;

    @Column(nullable = false, unique = true)     // 이메일
    private String email;

    @Column(nullable = false, length = 3000)
    private String description;                  // 가게 설명

    @Column(nullable = false)
    private LocalDateTime openingTime;           // 여는 시간

    @Column(nullable = false)
    private LocalDateTime closingTime;           // 닫는 시간

    @Column(nullable = false)
    private LocalDateTime lastOrder;             // 라스트 오더

    private String phoneNumber;                  // 가게 연락처

    private LocalDateTime breakTimeStart;        // 브레이크 타임 시작

    private LocalDateTime breakTimeEnd;          // 브레이크 타임 끗

    private Long deposit;                        //예약금

    private LocalDate holiday;                   // 휴무일

    private Integer capacity;                    // 최대 수용 인원

    private Integer rating;                      // 별점

    private LocalDateTime updatedTime;          // 정보 수정 시간

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RestaurantStatus restaurantStatus = RestaurantStatus.ACTIVE;        // 회원이 활성화 상태인지 아닌지 확인하는 것

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AlcoholSelling alcoholSelling = AlcoholSelling.YES;       // 주류판매여부

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RestaurantRole role = RestaurantRole.RESTAURANT;          // role

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DepositAvailable depositAvailable = DepositAvailable.NO;  // 예약금 여부

    @Enumerated(EnumType.STRING)
    private RestaurantType restaurantType;                           // Restaurant 종류(한,중,일,양,외)

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "restaurant_address_id")                      // Restaurant 테이블이 외래 키를 가짐
    private RestaurantAddress address;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<RestaurantPhoto> photos = new ArrayList<>();       //레스토랑 사진

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();               // 레스토랑 리뷰

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<RestaurantInfo> infos = new ArrayList<>();               // 레스토랑 리뷰


    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks = new ArrayList<>();            // 레스토랑 북마크

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Menu> menus = new ArrayList<>();                   //메뉴

    public RestaurantListRes listDtoFromEntity() {
        double averageRating = reviews.isEmpty() ? 0.0 : reviews.stream().mapToDouble
                (r -> r.getRating().getValue()).average().orElse(0.0);
        return RestaurantListRes.builder()
                .id(this.id)                                            // 레스토랑 Id
                .name(this.name)                                        // 레스토랑 이름
                .averageRating(averageRating)                           // 레스토랑 평균 별점
                .bookmarkCount((long) this.bookmarks.size())            // 레스토랑 즐겨찾기 한 사람 수
                .reviewCount(this.reviews.size())                       // 리뷰 수
                .address(this.address.getFullAddress())                 // 주소
                .imagePath(this.photos.get(0).getPhotoUrl())            // 사진 url
                .restaurantType(this.restaurantType.toString())         // 레스토랑 타입
                .build();
    }

    public void updateProfile(RestaurantUpdateReq dto,String password){
        if(dto.getName() != null) this.name = dto.getName();        // 안고치면 안바뀜
        if(dto.getEmail() != null) this.email = dto.getEmail();
        if(dto.getPassword() != null) this.password = password;
        if(dto.getPhoneNumber() != null) this.phoneNumber = dto.getPhoneNumber();
        if(dto.getDescription() != null) this.description = dto.getDescription();
        if(dto.getOpeningTime() != null) this.openingTime = dto.getOpeningTime();
        if(dto.getClosingTime() != null) this.closingTime = dto.getClosingTime();
        if(dto.getBreakTimeStart() != null) this.breakTimeStart = dto.getBreakTimeStart();
        if(dto.getBreakTimeEnd() != null) this.breakTimeEnd = dto.getBreakTimeEnd();
        if(dto.getLastOrder() != null) this.lastOrder=dto.getLastOrder();
        if(dto.getHoliday() != null) this.holiday = dto.getHoliday();
        if(dto.getCapacity() != 0) this.capacity = dto.getCapacity();
        if(dto.getRestaurantType() != null) this.restaurantType = dto.getRestaurantType();
        if(dto.getAddress() != null) {
                this.address.setCity(dto.getAddress().getCity());
                this.address.setStreet(dto.getAddress().getStreet());
        }
        // 예약금 관련 처리
        if (dto.getDepositAvailable() != null) {
            this.depositAvailable = DepositAvailable.valueOf(dto.getDepositAvailable().toUpperCase());
            if (this.depositAvailable == DepositAvailable.NO) {
                this.deposit = null;
            } else {
                this.deposit = dto.getDeposit();
            }
        }
    }

    public RestaurantDetailRes detailFromEntity() {
        double averageRating = reviews.isEmpty() ? 0.0 : reviews.stream().mapToDouble
                (r -> r.getRating().getValue()).average().orElse(0.0);
        return RestaurantDetailRes.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .description(this.description)
                .phoneNumber(this.phoneNumber)
                .address(this.address.getFullAddress())
                .averageRating(averageRating)
                .bookmarkCount((long)this.bookmarks.size())
                .updatedTime(this.updatedTime)
                .build();
    }



    public void addPhotos(List<RestaurantPhoto> newPhotos) {
        for (RestaurantPhoto photo : newPhotos) {
            if (!this.photos.contains(photo)) {
                this.photos.add(photo);
            }
        }
    }

    public void removePhotos(List<String> photoUrlsToRemove) {

        // Iterator 인터페이스는 list나 set, map같은 컬렉션요소에서 순차적으로 접근하고 조작할 수 있는 인터페이스.
        // 특정 요소에 접근해서 삭제나 요소 여부 확인 등을 할 때 용이함. list를 사용할 때 발생되는 예외처리를 미리 막아주는 역할을 함.

        Iterator<RestaurantPhoto> iterator = this.photos.iterator();
        while (iterator.hasNext()) {
            RestaurantPhoto photo = iterator.next();
            if (photoUrlsToRemove.contains(photo.getPhotoUrl())) {
                photo.photoDeactivate();  // Soft Delete
                iterator.remove();
            }
        }
    }

    public void deleteRestaurant() {
        this.restaurantStatus = RestaurantStatus.INACTIVE;
    }

    public void setPhotos(List<RestaurantPhoto> photos) {
        this.photos = photos;
    }



}
