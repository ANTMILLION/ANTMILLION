package com.antmillion.user.dto;

import lombok.*;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRankResponseDTO {
    private String rankName;           // 현재 랭크
    private String rankImage;          // 랭크 이미지 경로
    private int currentPoint;          // 현재 사용자 포인트
    private int nextRankPoint;         // 다음 랭크 목표 포인트
    private int neededPoint;           // 다음 랭크까지 남은 포인트
    private int currentRankStartPoint; // 현재 랭크의 시작 포인트
}
