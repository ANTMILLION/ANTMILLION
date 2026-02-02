package com.antmillion.mission.dto;

import lombok.*;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MissionStatusResponseDTO {
    private int totalProgress;
    private int quizCount;
    private int newsCount;
    private boolean QuizCompleted;
    private boolean NewsCompleted;
}