package com.antmillion.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizChoiceDTO {
	private Long quiz_choice_id;
    private Long quiz_id;
    private Integer choice_no;
    private String choice_text;
}
