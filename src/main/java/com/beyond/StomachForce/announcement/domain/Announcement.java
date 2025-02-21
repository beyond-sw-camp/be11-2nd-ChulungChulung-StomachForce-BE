package com.beyond.StomachForce.announcement.domain;


import com.beyond.StomachForce.Common.domain.BaseReservationTimeEntity;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.accouncementImage.domain.AnnouncementImage;
import com.beyond.StomachForce.announcement.dtos.AnnouncementUpdateReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString(exclude = "images")
@Entity
@AllArgsConstructor
@Builder
public class Announcement extends BaseReservationTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String contents;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Type type = Type.ANNOUNCE;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AnnounceStatus status = AnnounceStatus.ON;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @JsonIgnore
    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<AnnouncementImage> images = new ArrayList<>();

    public void setImages(List<AnnouncementImage> announcementImages) {
        this.images = announcementImages;
    }


    public void updateAnnouncement(String newTitle, String newContents, AnnounceStatus newStatus, List<AnnouncementImage> newImages) {
        if (newTitle != null && !newTitle.isBlank()) {
            this.title = newTitle;
        }
        if (newContents != null && !newContents.isBlank()) {
            this.contents = newContents;
        }
        if (newStatus != null) {
            this.status = newStatus;
        }
        if (newImages != null) {
            updateImages(newImages);
        }
    }

    /**
     * 🔥 기존 이미지와 새로운 이미지를 병합하여 업데이트 (Setter 없이 안전한 방식)
     */
    private void updateImages(List<AnnouncementImage> newImages) {
        // 기존 이미지 중에서 새로운 리스트에 없는 이미지는 삭제
        images.removeIf(existingImage ->
                newImages.stream().noneMatch(newImage -> newImage.getImagePath().equals(existingImage.getImagePath()))
        );

        // 새로운 이미지 리스트에만 존재하는 이미지는 추가
        for (AnnouncementImage newImage : newImages) {
            boolean exists = images.stream()
                    .anyMatch(existingImage -> existingImage.getImagePath().equals(newImage.getImagePath()));

            if (!exists) {
                newImage.setAnnouncement(this); // 관계 설정
                images.add(newImage);
            }
        }

    }

}

