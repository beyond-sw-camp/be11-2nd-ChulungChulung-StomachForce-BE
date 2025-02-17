package com.beyond.StomachForce.serviceCenter.dtos;

import com.beyond.StomachForce.serviceCenter.domain.ServicePost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ServicePostResDto {
    private Long id;
    private Long userId;
    private String title;
    private String contents;
    private String category;
    private String visibility;

    public ServicePostResDto(ServicePost post) {
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.category = post.getCategory().name();
        this.visibility = post.getVisibility().name();
    }
}
