// 주식당일분봉조회-차트당일분봉
package com.antmillion.kis.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.antmillion.kis.dto.DayMinuteFlatDTO;
import com.antmillion.kis.service.DayMinuteQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController // JSON 응답 처리에 유용함
@RequestMapping("/api/stock")
public class DayMinuteChartController {
	private final DayMinuteQueryService stockQueryService;

	@GetMapping("/candle/{stockCode}")
	public List<DayMinuteFlatDTO> getCandleData(
			@PathVariable("stockCode") String stockCode,
			@RequestParam(value = "interval", defaultValue = "1") String interval) { // 1분봉 
		return stockQueryService.getChartData(stockCode, Integer.parseInt(interval));
	}
}
