package com.antmillion.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageDTO {
	private Long message_id;
    private String message_content;
    private String message_type;
}
