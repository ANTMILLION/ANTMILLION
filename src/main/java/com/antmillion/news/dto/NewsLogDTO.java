package com.antmillion.news.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewsLogDTO {
    private Long newsLogId;
    private Long userId;
    private String newsUrl;
    private LocalDateTime newsReadAt;
}