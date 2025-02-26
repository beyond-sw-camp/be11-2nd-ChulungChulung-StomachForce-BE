package com.beyond.StomachForce.reservation.domain;

import com.beyond.StomachForce.menu.domain.Menu;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private Integer quantity; // ✅ 주문한 메뉴의 수량 저장

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}