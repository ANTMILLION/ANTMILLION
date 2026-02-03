package com.antmillion.news.dto;

import com.antmillion.user.dto.UserRankResponseDTO;
import lombok.*;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewsRewardResponseDTO {
    private int progress;
    private int earnedPoint;
    private UserRankResponseDTO updatedRank;
}
