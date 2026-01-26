package com.antmillion.mission.dto;

import lombok.*;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizResultDTO {
    private Integer answer;
    private String explanation;
    private Integer point;
}
