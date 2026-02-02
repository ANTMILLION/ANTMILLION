package com.antmillion.news.dto;

import lombok.*;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewsProgressResponseDTO {
    private int readCount;       // 오늘 읽은 개수
    private int progress;        // 달성률
    private Integer earnedPoint; // 획득 포인트 (읽기 요청 시에만 포함)
}
