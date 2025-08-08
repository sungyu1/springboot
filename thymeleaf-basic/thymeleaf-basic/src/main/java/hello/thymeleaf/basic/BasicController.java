package hello.thymeleaf.basic;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/basic")
public class BasicController {
//    텍스트를 보여주는 메서드 
    @GetMapping("text-basic")
    public String textBasic(Model model){
        model.addAttribute("date","모오지");
        return "basic/text-basic";
    }
//    텍스트를 강조해서 보여주는 메서드 
    @GetMapping("text-unescaped")
    public String textUnescaped(Model model){
        model.addAttribute("data","모오지 <b>모긴모야</b>");
        return "basic/text-unescaped";
    }
    @GetMapping("/variable")
    public String variable(Model model){
        User userA = new User("userA", 30);
        User userB = new User("userB", 40);

        List<User> list = new ArrayList<>();
        list.add(userA);
        list.add(userB);

        Map<String,User> map=new HashMap<>();
        map.put("userA", userA);
        map.put("userB", userB);

        model.addAttribute("user", userA);
        model.addAttribute("users", list);
        model.addAttribute("userMap", map);
        return"basic/variable";
    }



    @GetMapping("basic-objects")
    public String basicObjects(HttpSession session, HttpServletRequest request, Model model) {

        session.setAttribute("sessionData", "Hello Session");
        model.addAttribute("sessionData", session.getAttribute("sessionData")); // 세션 데이터
        model.addAttribute("paramData", request.getParameter("paramData"));     // 요청 파라미터
        return "basic/basic-objects";
    }

    @GetMapping("date")
    public String date(Model model){
        model.addAttribute("localDateTime", LocalDateTime.now());
        return "basic/date";
    }

    @Component("helloBean")
    static class HelloBean{
        public String hello(String data){
            return "Hello" + data;
        }
    }

//  타임리트 URL 이동
    @GetMapping("link")
    public String link(Model model){
        model.addAttribute("param1", "data1");
        model.addAttribute("param2", "data2");
        return "basic/link";
    }

//  타임리프 리트럴
    @GetMapping("/literal")
    public String literal(Model model){
        model.addAttribute("data","이게맞나?");
        return "basic/literal";
    }

//  타임리프 연산
    @GetMapping("/operation")
    public String operation(Model model){
        model.addAttribute("null", null);
        model.addAttribute("data", "Data가있네");
        return"basic/operation";
    }

//  타임리프 속성값 설정
    @GetMapping("/attribute")
    public String attribute(){
        return "basic/attribute";
    }

//   타임리프 반복문
    @GetMapping("/each")
    public String each(Model model){
        addUsers(model);
        return "basic/each";
    }
//  반복문 테스트를 위한 data
    private  void addUsers(Model model){
        List<User>list=new ArrayList<>();
        list.add(new User("userA",10));
        list.add(new User("userB",20));
        list.add(new User("userC",30));
        list.add(new User("userD",50));

        model.addAttribute("users", list);
    }

//   타임리프 조건부 평가
    @GetMapping("/condition")
    public String condition(Model model){
        addUsers(model);
        return "basic/condition";
    }

//    타임리프 주석
    @GetMapping("/comments")
    public String comments(Model model){
        model.addAttribute("data","보이나?");
        return "basic/comments";
    }

//  타임리프 블록
    @GetMapping("/block")
    public String block(Model model){
        addUsers(model);
        return "basic/block";
    }

//   자바스크립트 인라인
    @GetMapping("/javascript")
    public String javascript(Model model){
        model.addAttribute("user", new User("userG",100));
        addUsers(model);
        return "basic/javascript";
    }




//    내부에서 테스트 해볼 데이터
    @Data
    static class User{
        private String username;
        private int age;

        public User(String username, int age){
            this.username=username;
            this.age=age;
        }
}
}
