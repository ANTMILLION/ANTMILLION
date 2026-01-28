package com.antmillion.news.mapper;

import com.antmillion.news.dto.NewsLogDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewsMapper {
    // 오늘 이 뉴스를 읽었는지 확인
    boolean existsTodayLog(@Param("userId") Long userId, @Param("newsUrl") String newsUrl);

    // 뉴스 읽음 기록 저장
    void insertNewsLog(NewsLogDTO newsLogDTO);;

    // 오늘 읽은 뉴스 개수 조회
    int countTodayNewsRead(@Param("userId") Long userId);

    // 오늘 읽은 기사 url 조회
    List<String> selectTodayReadUrls(@Param("userId") Long userId);
}