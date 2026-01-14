package com.antmillion.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizQuestionDTO {
	private Long quiz_id;
    private Long mission_id;
    private Integer type;
    private String question;
    private Integer answer;
    private String explanation;
    private Integer point;
    private LocalDateTime date;
}
