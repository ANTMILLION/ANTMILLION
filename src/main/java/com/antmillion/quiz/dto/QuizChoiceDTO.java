package com.antmillion.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizChoiceDTO {
	private Long quizChoiceId;
    private Long quizId;
    private Integer choiceNo;
    private String choiceText;
}
