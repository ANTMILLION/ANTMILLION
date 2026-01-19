package com.antmillion.communication.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommunityDetailResponseDTO {
    private Long postId;
    private Long userId;
    private String nickname;        
    private String stockCode;
    private String stockName;       
    private String content;
    private LocalDateTime postedDate;
}