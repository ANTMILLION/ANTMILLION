package com.antmillion.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDTO {
	private Long accountId;
    private Long userId;
    private String accountNumber;
    private Long balance;
}