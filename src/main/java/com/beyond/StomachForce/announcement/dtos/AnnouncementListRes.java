package com.beyond.StomachForce.announcement.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AnnouncementListRes {
    private String title;
    private LocalDate creaetedDate;
    private String announcementType;
}
