package jpabook.jpashop.controller;

import jakarta.servlet.http.HttpSession;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.VacationRequest;
import jpabook.jpashop.domain.VacationType;
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
        String deptName = switch (loginMember.getDeptCode() != null ? loginMember.getDeptCode() : "") {
            case "im" -> "내과";
            case "gs" -> "외과";
            case "ns" -> "신경외과";
            case "os" -> "정형외과";
            case "cs" -> "흉부외과";
            case "ms" -> "진료지원센터";
            case "am" -> "관리자";
            default -> "미지정";
        };

        String jobLevelName = switch (loginMember.getJobLevel()) {
            case "1" -> "사원";
            case "2" -> "부서장";
            case "3" -> "원장";
            case "4" -> "진료지원 센터장";
            case "5" -> "행정원장";
            case "6" -> "대표원장";
            case "7" -> "관리자";
            case "8" -> "전무";
            default -> "미지정";
        };

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("deptName", deptName);
        model.addAttribute("jobLevelName", jobLevelName);
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

        // 부서 정보가 없는 경우 처리
        if (loginMember.getDeptCode() == null) {
            model.addAttribute("error", "부서 정보가 없습니다. 관리자에게 문의하세요.");
            return "error/error";
        }

        List<Member> substitutes = memberService.findByDeptAndJob(loginMember.getDeptCode(), loginMember.getJobLevel());
        List<Member> hrStaff = memberService.findHrStaff(); // 인사담당자 목록

        model.addAttribute("vacationRequestForm", new VacationRequestForm());
        model.addAttribute("substitutes", substitutes);
        model.addAttribute("hrStaff", hrStaff);
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("vacationTypes", VacationType.values());
        setLocalizedMemberInfo(loginMember, model);
        return "vacations/vacationsForm";
    }

    @PostMapping("/vacation/submit")
    public String submitVacation(@ModelAttribute VacationRequestForm form,
                                 HttpSession session) {
        Member applicant = (Member) session.getAttribute("loginMember");
        
        // 부서 정보가 없는 경우 처리
        if (applicant.getDeptCode() == null) {
            return "redirect:/vacation/form?error=dept";
        }
        
        Member substitute = memberService.findOne(form.getSubstituteId());
        Member hrStaff = memberService.findOne(form.getHrStaffId());

        // 인사담당자가 선택되지 않은 경우
        if (hrStaff == null) {
            return "redirect:/vacation/form?error=hr";
        }

        try {
            vacationService.submitVacation(applicant, substitute, hrStaff, form);
            return "redirect:/approval/my-requests";
        } catch (IllegalStateException e) {
            // 부서장을 찾을 수 없는 경우
            return "redirect:/vacation/form?error=leader";
        }
    }

}
