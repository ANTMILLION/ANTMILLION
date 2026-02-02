package com.antmillion.quiz.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizLogDTO {
	private Long quizLogId;
    private Long userId;
    private Long quizId;
    private LocalDateTime solvedAt;
}
