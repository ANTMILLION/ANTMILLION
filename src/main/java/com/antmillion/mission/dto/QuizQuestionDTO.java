package com.antmillion.mission.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizQuestionDTO {
	private Long quizId;
    private Long missionId;
    private Integer type;
    private String question;
    private Integer answer;
    private String explanation;
    private Integer point;
    private LocalDateTime quizDate;

    // 객관식일 경우 보기들을 담아서 화면에 보내줄 리스트
    private List<QuizChoiceDTO> choices;
}
