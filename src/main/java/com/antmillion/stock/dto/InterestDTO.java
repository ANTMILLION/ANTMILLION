package com.antmillion.stock.dto;

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
public class InterestDTO {
	private String stock_code;
    private Long account_id;
    private LocalDateTime created_at;
}
