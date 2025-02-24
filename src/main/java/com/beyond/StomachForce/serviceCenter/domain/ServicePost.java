package com.beyond.StomachForce.serviceCenter.domain;

import com.beyond.StomachForce.Common.domain.BaseReservationTimeEntity;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.serviceCenter.domain.select.Category;
import com.beyond.StomachForce.serviceCenter.domain.select.Visibility;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
@Setter
public class ServicePost extends BaseReservationTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    private Category category;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String contents;
    @Enumerated(EnumType.STRING)
    private Visibility visibility;


    @OneToMany(mappedBy = "servicePost",cascade = CascadeType.ALL)
    @Builder.Default
    private List<ServicePostPhoto> servicePostPhotos = new ArrayList<>();

}
