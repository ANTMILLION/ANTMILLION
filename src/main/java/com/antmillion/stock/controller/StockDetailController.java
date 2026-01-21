package com.antmillion.stock.controller;

import com.antmillion.stock.dto.StockDTO;
import com.antmillion.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockDetailController {

	private final StockService stockService;

	@GetMapping("/detail")
	public String stockDetail(@RequestParam("code") String code, Model model) {
		StockDTO stockDTO = stockService.getStockByCode(code);
		model.addAttribute("stock", stockDTO);
		return "stock/detail";
	}
}