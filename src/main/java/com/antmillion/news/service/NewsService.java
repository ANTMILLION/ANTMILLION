package com.antmillion.news.service;

import com.antmillion.news.dto.NewsProgressResponseDTO;
import com.antmillion.news.dto.NewsRewardResponseDTO;

import java.util.List;

public interface NewsService {
    NewsRewardResponseDTO readNews(Long userId, String newsUrl);
    NewsProgressResponseDTO getNewsProgress(Long userId);
    List<String> selectTodayReadUrls(Long userId);
}