package com.antmillion.naver.dto;

import lombok.*;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NaverNewsItemDTO {
    private String title;
    private String link;
    private String description;
    private String pubDate;
}
