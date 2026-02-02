package com.antmillion.news.service;

import com.antmillion.news.dto.NewsProgressResponseDTO;
import java.util.List;

public interface NewsService {
    NewsProgressResponseDTO readNews(Long userId, String newsUrl);
    NewsProgressResponseDTO getMissionProgress(Long userId);
    List<String> selectTodayReadUrls(Long userId);
}