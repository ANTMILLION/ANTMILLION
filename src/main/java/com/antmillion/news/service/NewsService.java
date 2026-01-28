package com.antmillion.news.service;

import java.util.List;
import java.util.Map;

public interface NewsService {
    Map<String, Object> readNews(Long userId, String newsUrl);
    Map<String, Object> getMissionProgress(Long userId);
    List<String> selectTodayReadUrls(Long userId);
}