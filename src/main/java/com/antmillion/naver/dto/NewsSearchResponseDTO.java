package com.antmillion.naver.dto;

import lombok.*;

import java.util.List;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewsSearchResponseDTO {
    private int totalCount;
    private List<NaverNewsItemDTO> articles;

    public void assignTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void assignArticles(List<NaverNewsItemDTO> articles) {
        this.articles = articles;
    }
}