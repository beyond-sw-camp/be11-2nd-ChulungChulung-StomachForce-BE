package com.beyond.StomachForce.allergyInfo.domain;


import com.beyond.StomachForce.menu.domain.Menu;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@ToString
@Entity
@AllArgsConstructor
@Builder
public class AllergyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "allergyInfo")
    private Menu menu;

    @Enumerated(EnumType.STRING)
    private Milk milk;
    @Enumerated(EnumType.STRING)
    private Egg egg;
    @Enumerated(EnumType.STRING)
    private Wheat wheat;
    @Enumerated(EnumType.STRING)
    private Soy soy;
    @Enumerated(EnumType.STRING)
    private Peanut peanut;
    @Enumerated(EnumType.STRING)
    private Nuts nuts;
    @Enumerated(EnumType.STRING)
    private Fish fish;
    @Enumerated(EnumType.STRING)
    private Shellfish shellfish;

}
