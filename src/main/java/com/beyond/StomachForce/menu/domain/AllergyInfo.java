package com.beyond.StomachForce.menu.domain;


import com.beyond.StomachForce.menu.domain.select.*;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@ToString
@Entity
@AllArgsConstructor
@Builder
@Setter
public class AllergyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public AllergyInfo toEntity(){
        return AllergyInfo.builder()
                .milk(this.milk)
                .egg(this.egg)
                .wheat(this.wheat)
                .soy(this.soy)
                .peanut(this.peanut)
                .nuts(this.nuts)
                .fish(this.fish)
                .shellfish(this.shellfish)
                .build();
    }
}
