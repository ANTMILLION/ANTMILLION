// 주식당일분봉조회-차트당일분봉
package com.antmillion.kis.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.antmillion.kis.dto.DayMinuteFlatDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DayMinuteRedisChartService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    // 오늘 날짜 가져오기 (매일 자동으로 바뀜)
    private String getTodayKey(String stockCode) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "stock:chart:1min:" + stockCode + ":" + today;
    }

    // 데이터 누적해서 쌓기 (RPUSH)
    public void insertMinuteData(String stockCode, DayMinuteFlatDTO stockMinuteFlatDTO) {
        // 키 형식: stock:chart:1min:종목코드:yyyyMMdd
	    String key = getTodayKey(stockCode);
	    
	    // 똑같은 시간의 데이터를 또 가져오거나 가져오지 못했을 때 Redis 리스트의 마지막 값과 비교
	    List<Object> lastDataList = redisTemplate.opsForList().range(key, -1, -1);
	    
	    if (!lastDataList.isEmpty()) {
	        DayMinuteFlatDTO lastDto = (DayMinuteFlatDTO) lastDataList.get(0);
	        // 시간이 똑같으면 저장하지 않고 리턴
	        if (lastDto.getStckCntgHour().equals(stockMinuteFlatDTO.getStckCntgHour())) {
	            return; 
	        }
	    }

        // 시간이 다를 때만 저장
        redisTemplate.opsForList().rightPush(key, stockMinuteFlatDTO); // 리스트 오른쪽(끝)에 데이터 추가
        redisTemplate.expire(key, 24, TimeUnit.HOURS); // 하루치니까 24시간 뒤 자동 삭제
    }

    // 누적된 전체 데이터 가져오기 (LRANGE)
    public List<Object> getFullChart(String stockCode) {
        String key = getTodayKey(stockCode);

        // 0번부터 -1(끝)까지 전체 리스트 반환
        return redisTemplate.opsForList().range(key, 0, -1);
    }
}
