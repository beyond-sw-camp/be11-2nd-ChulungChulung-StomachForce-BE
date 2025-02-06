package com.beyond.StomachForce.restaurant.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
//      즐찾 추가 삭제를 위해서 즐겨찾기 상태를 전달하기 위함임.
public class BookmarkRes {
    private String id;              //레스토랑id
    private Boolean isBookmarked;
}
