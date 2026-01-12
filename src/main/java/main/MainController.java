package main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping({"/", "/antmillion", "/antmillion/"})
@Controller
public class MainController {

    @GetMapping
    public String mainPage() {
        return "main/main";
    }

}
