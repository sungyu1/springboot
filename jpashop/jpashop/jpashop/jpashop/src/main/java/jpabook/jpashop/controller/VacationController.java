package jpabook.jpashop.controller;

import jakarta.servlet.http.HttpSession;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.VacationRequest;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class VacationController {

    private final VacationService vacationService;
    private final MemberService memberService;
    private void setLocalizedMemberInfo(Member loginMember, Model model) {
        String deptName = switch (loginMember.getDeptCode()) {
            case "im" -> "내과";
            case "gs" -> "외과";
            case "ns" -> "신경외과";
            case "os" -> "정형외과";
            case "cs" -> "흉부외과";
            case "ms" -> "진료지원센터";
            case "am" -> "관리자";
            default -> "미지정";
        };

        String jobTypeName = switch (loginMember.getJobType()) {
            case 1 -> "사원";
            case 2 -> "부서장";
            case 3 -> "원장";
            case 4 -> "진료지원";
            case 5 -> "센터장";
            case 6 -> "관리자";
            default -> "미지정";
        };

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("deptName", deptName);
        model.addAttribute("jobTypeName", jobTypeName);
    }

    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/members/login";

        setLocalizedMemberInfo(loginMember, model);
        return "index";
    }

    @GetMapping("/vacation/form")
    public String showVacationForm(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/members/login";


        List<Member> substitutes = memberService.findByDeptAndJob(loginMember.getDeptCode(), loginMember.getJobType());
        List<Member> deptLeaders = memberService.findByDeptAndJob(loginMember.getDeptCode(), 2); // jobType 2 = 부서장

        model.addAttribute("vacationRequestForm", new VacationRequestForm());
        model.addAttribute("substitutes", substitutes);
        model.addAttribute("deptLeaders", deptLeaders);
        model.addAttribute("loginMember", loginMember);
        setLocalizedMemberInfo(loginMember, model);
        return "vacations/vacationsForm";
    }

    @PostMapping("/vacation/submit")
    public String submitVacation(@ModelAttribute VacationRequestForm form,
                                 HttpSession session) {
        Member applicant = (Member) session.getAttribute("loginMember");
        Member substitute = memberService.findOne(form.getSubstituteId());
        Member deptLeader = memberService.findOne(form.getDeptLeaderId());

        vacationService.submitVacation(applicant, substitute, deptLeader, form);
        return "redirect:/vacation/list";
    }

}
