package jpabook.jpashop.controller;

import jakarta.servlet.http.HttpSession;
import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
class LoginMemberAdvice {

    @ModelAttribute("loginMember")
    public Member loginMember(HttpSession session){
        return (Member) session.getAttribute("loginMember");
    }
}
