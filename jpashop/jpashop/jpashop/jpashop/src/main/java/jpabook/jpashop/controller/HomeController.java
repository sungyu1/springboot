package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@Slf4j
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "home"; // 홈페이지로 시작
    }

    @RequestMapping("/index")
    public String showIndexPage() {
        return "index"; // templates/index.html 렌더링
    }
//    @RequestMapping("/")
//    public String home(){
//        log.info("지금은 HomeController 입니다.");
//        return "home";
//    }

}
