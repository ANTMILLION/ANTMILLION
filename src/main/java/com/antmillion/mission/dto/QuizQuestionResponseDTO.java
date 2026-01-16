package com.antmillion.mission.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizQuestionResponseDTO {

    private Long quizId;
    private Integer type;
    private String question;
    private Integer point;
    private LocalDateTime quizDate;

    // 객관식일 경우 보기들을 담아서 화면에 보내줄 리스트
    private List<QuizChoiceDTO> choices;
}