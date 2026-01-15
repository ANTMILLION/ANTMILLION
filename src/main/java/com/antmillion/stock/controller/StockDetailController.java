package com.antmillion.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stock")
public class StockDetailController {
	@GetMapping("/detail")
	public String stockDetail() {
		return "stock/detail";
	}
}