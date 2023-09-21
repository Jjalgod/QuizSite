package com.example.koskQuizProgram.mainPage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class MainpageController {
    @RequestMapping("/main")
    public String mainPage() {
        return "main/main";
    }

    @RequestMapping("/loginRequired")
    public String loginRequired(){
        return "main/loginRequired";
    }
}
