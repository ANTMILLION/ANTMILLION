package com.antmillion.naver.dto;

import lombok.*;

import java.util.List;

@Getter@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NaverNewsListResponseDTO {
    private List<NaverNewsItemDTO> articles; // 뉴스 리스트
    private int totalCount;                  // 전체 개수
    private int currentPage;                 // 현재 페이지
    private int maxPage;                     // 마지막 페이지
    private List<String> readList;           // 내가 읽은 URL 목록
}
