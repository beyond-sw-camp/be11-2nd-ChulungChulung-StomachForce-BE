package com.beyond.StomachForce.accouncementImage.domain;

import com.beyond.StomachForce.announcement.domain.Announcement;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@ToString(exclude = "announcement")
@Entity
@AllArgsConstructor
@Builder
public class AnnouncementImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;
    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }
}
