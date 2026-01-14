package com.antmillion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizChoiceDTO {
	private Long quiz_choice_id;
    private Long quiz_id;
    private Integer choice_no;
    private String choice_text;
}
