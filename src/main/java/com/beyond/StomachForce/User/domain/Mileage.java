package com.beyond.StomachForce.User.domain;

import com.beyond.StomachForce.Common.domain.BaseTimeEntity;
import com.beyond.StomachForce.Post.domain.Post;
import com.beyond.StomachForce.User.domain.Enum.EarnedMileage;
import com.beyond.StomachForce.User.domain.Enum.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@ToString
@Builder
public class Mileage extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private EarnedMileage earnedMileage;
    @Builder.Default
    private Long mileageAmount = 0L;
}
