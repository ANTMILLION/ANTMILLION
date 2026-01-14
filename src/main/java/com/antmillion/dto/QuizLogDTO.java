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
public class QuizLogDTO {
	private Long quiz_log_id;
    private Long user_id;
    private Long quiz_id;
    private LocalDateTime solved_at;
}
