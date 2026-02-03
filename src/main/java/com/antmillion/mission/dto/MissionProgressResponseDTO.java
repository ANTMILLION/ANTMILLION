package com.antmillion.mission.dto;

import lombok.*;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MissionProgressResponseDTO {
    private int totalProgress;
    private int quizCount;
    private int newsCount;
    private boolean QuizCompleted;
    private boolean NewsCompleted;
}