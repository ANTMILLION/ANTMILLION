package com.antmillion.mission.dto;

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
	private Long quiz_log_id;
    private Long user_id;
    private Long quiz_id;
    private LocalDateTime solved_at;
}
