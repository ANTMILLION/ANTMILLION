// 주식당일분봉조회-차트당일분봉
package com.antmillion.kis.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.antmillion.kis.dto.DayMinuteFlatDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DayMinuteQueryService {
	private final DayMinuteRedisChartService redisChartService;

	// [조회용] 컨트롤러 호출
	public List<DayMinuteFlatDTO> getChartData(String stockCode, int interval) {
		// Redis에 쌓인 전체 리스트 반환
		List<Object> fullChart = redisChartService.getFullChart(stockCode);
		List<DayMinuteFlatDTO> oneMinList = fullChart.stream().map(obj -> (DayMinuteFlatDTO) obj)
				.collect(Collectors.toList());

		if (interval <= 1 || oneMinList.isEmpty())
			return oneMinList;

		// N분봉 가공 로직
		Map<Integer, List<DayMinuteFlatDTO>> grouped = oneMinList.stream().collect(Collectors.groupingBy(dto -> {
			int hour = Integer.parseInt(dto.getStckCntgHour().substring(0, 2));
			int min = Integer.parseInt(dto.getStckCntgHour().substring(2, 4));
			int totalMinutes = (hour * 60) + min;
			return totalMinutes / interval;
		}, TreeMap::new, Collectors.toList()));

		return grouped.values().stream().map(subList -> {
			long high = subList.stream().mapToLong(DayMinuteFlatDTO::getStckHgpr).max().orElse(0L);
			long low = subList.stream().mapToLong(DayMinuteFlatDTO::getStckLwpr).min().orElse(0L);

			return DayMinuteFlatDTO.builder().stckCntgHour(subList.get(0).getStckCntgHour())
					.stckOprc(subList.get(0).getStckOprc()).stckPrpr(subList.get(subList.size() - 1).getStckPrpr())
					.stckHgpr(high).stckLwpr(low).build();
		}).collect(Collectors.toList());
	}
}
