package com.antmillion.history;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HIstoryController {

	@GetMapping("/history")
	public String f() {
		return "history/history";
	}
}
