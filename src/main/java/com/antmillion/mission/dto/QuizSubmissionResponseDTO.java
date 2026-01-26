package com.antmillion.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizSubmissionResponseDTO {
    @JsonProperty("isCorrect")
    private boolean isCorrect;
    private Integer point;
    private String message;
}
