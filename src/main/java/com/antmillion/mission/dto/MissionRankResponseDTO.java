package com.antmillion.mission.dto;

import lombok.*;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MissionRankResponseDTO {
    private int currentPoint;      // 현재 포인트
    private String rankName;       // 현재 랭크
    private int nextRankPoint;     // 다음 랭크 목표 포인트
    private int neededPoint;       // 다음 랭크까지 남은 포인트
}