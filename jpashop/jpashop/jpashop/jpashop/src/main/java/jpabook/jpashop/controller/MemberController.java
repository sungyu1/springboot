package jpabook.jpashop.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
//  회원가입 폼으로 이동
    @GetMapping(value = "/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }
//  회원가입 처리
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(),form.getZipcode());

        Member member = new Member();
        member.setId(form.getId()); // Oracle userid 와 유사하게 이메일을 ID로 임시 사용
        member.setName(form.getName());// 이름저장
        member.setEmail(form.getEmail());//이메일저장
        member.setPassword(form.getPassword()); //패스워드 저장
        member.setPhone(form.getPhone()); //전화번호 저장
        member.setAddress(address);//주소저장
        member.setSignatureImage(form.getSignatureData());// 싸인저장
        member.setDeptCode(form.getDeptCode());   // 부서 코드 저장
        member.setJobType(form.getJobType());     // 직무유형 저장
        member.setUseFlag(form.getUseFlag());     // 근무상태 저장

        try {
            memberService.join(member);
        } catch (IllegalStateException e) {
            result.rejectValue("id", "duplicate", e.getMessage()); // ← id 필드에 바인딩된 에러 추가
            return "members/createMemberForm"; // 다시 회원가입 폼으로 이동
        }

        return "redirect:/";
    }
//  전체회원 목록조회
    @GetMapping("/admin/memberList")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
// 아이디 중복체크
    @GetMapping("/members/check-id")
    @ResponseBody
    public String checkIdDuplicate(@RequestParam("id") String id) {
        boolean available = memberService.isIdAvailable(id);
        return available ? "AVAILABLE" : "DUPLICATE";
    }

}
