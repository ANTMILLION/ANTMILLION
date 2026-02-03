package com.antmillion.quiz.dto;

import lombok.*;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizProgressResponseDTO {
    private int solvedCount;  // 오늘 푼 개수
    private int totalCount;   // 총 문제 수
    private int progress;     // 진행률 (%)
}
