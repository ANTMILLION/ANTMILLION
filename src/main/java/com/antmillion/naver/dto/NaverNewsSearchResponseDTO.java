package com.antmillion.naver.dto;

import lombok.*;

import java.util.List;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NaverNewsSearchResponseDTO {
    private int total;
    private List<NaverNewsItemDTO> items;
}