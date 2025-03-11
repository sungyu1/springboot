package hello.springmcc.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpringMemberFormControllerV1 {
    @RequestMapping("/springmcc/v1/members/new-from");
    public ModelAndView process(){
        public ModelAndView("new-from");
    }
}
