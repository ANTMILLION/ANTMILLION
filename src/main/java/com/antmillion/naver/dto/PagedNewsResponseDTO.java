package com.antmillion.naver.dto;

import lombok.*;

import java.util.List;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PagedNewsResponseDTO {
    // 실제 뉴스 목록
    private List<NaverNewsItemDTO> articles;
    // 전체 뉴스 개수 (누적된 총 개수)
    private int totalCount;
    // 현재 페이지 (1부터 시작)
    private int currentPage;
    // 전체 페이지 수
    private int maxPage;
}