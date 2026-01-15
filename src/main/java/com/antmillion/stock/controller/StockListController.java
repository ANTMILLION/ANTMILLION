package com.antmillion.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/stocklist")
@Controller
public class StockListController {

    @GetMapping
    public String stockListPage() {
        return "stocklist/stocklist";
    }

}
