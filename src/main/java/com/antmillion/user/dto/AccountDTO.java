package com.antmillion.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDTO {
	 private Long account_id;
	    private Long user_id;
	    private String account_number;
	    private Long balance;
}