package com.beyond.StomachForce.announcement.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventBannerRes {
    private Long eventId;
    private String eventTitle;
    private String eventImage;
}
