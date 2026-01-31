package com.antmillion.quiz.dto;

import lombok.*;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizSubmissionRequestDTO {
    private Long userId;
    private Long quizId;
    private Integer choiceNo;
}
