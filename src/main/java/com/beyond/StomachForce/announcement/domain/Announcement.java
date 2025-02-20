package com.beyond.StomachForce.announcement.domain;


import com.beyond.StomachForce.Common.domain.BaseReservationTimeEntity;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.accouncementImage.domain.AnnouncementImage;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@Entity
@AllArgsConstructor
@Builder
public class Announcement extends BaseReservationTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String contents;
    @Enumerated
    @Builder.Default
    private Type type = Type.ANNOUNCE;
    @Enumerated
    @Builder.Default
    private AnnounceStatus status = AnnounceStatus.ON;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @JsonManagedReference
    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<AnnouncementImage> images = new ArrayList<>();

    public void setImages(List<AnnouncementImage> announcementImages) {
        this.images = announcementImages;
    }

}

