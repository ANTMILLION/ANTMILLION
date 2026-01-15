package com.antmillion.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageDTO {
	private Long messageId;
    private String messageContent;
    private String messageType;
}
