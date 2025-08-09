package jpabook.jpashop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.exception.BadCredentialsException;
import jpabook.jpashop.exception.NoSuchMemberException;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private  final MemberService memberService;


//    로그인 버튼 누르면 로그인 폼으로 이동
    @GetMapping("/members/login")// home.html 에서 로그인 버튼을 누르면 발생하는 함수
    public  String loginForm(Model model){
        model.addAttribute("loginForm", new LoginForm());
        return "members/loginForm";// loginFoem 으로 이동
    }

//  로그인 기능 메서드
@PostMapping("/members/login")
public String login(@ModelAttribute("loginForm") LoginForm loginForm,
                    HttpSession session, Model model) {
    try {
        Member member = memberService.login(loginForm.getId(), loginForm.getPassword());
        session.setAttribute("loginMember", member);
        return "redirect:/index";
    } catch (NoSuchMemberException e) {
        model.addAttribute("signupRecommend", true); // 아이디 없음 → 회원가입 유도 모달
        return "members/loginForm";
    } catch (BadCredentialsException e) {
        model.addAttribute("badCredentials", true);  // 아이디 있음/비번 틀림 → 안내 모달
        return "members/loginForm";
    }
}

//    로그아웃 기능 메서드
@GetMapping("/members/logout")
public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
    session.invalidate(); // 세션 무효화
    redirectAttributes.addFlashAttribute("logoutSuccess", true);
    return "redirect:/";
}

    @GetMapping("/")
    public String home(){
        return "home";
    }

}
