package jpabook.jpashop.controller;

import jpabook.jpashop.domain.ApprovalStatus;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.VacationRequest;
import jpabook.jpashop.service.ApprovalService;
import jpabook.jpashop.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/approval")
public class ApprovalController {

    private final ApprovalService approvalService;
    private final VacationService vacationService;

    // 결재 대기 목록 조회
    @GetMapping("/pending")
    public String pendingApprovals(Model model, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }

        List<VacationRequest> pendingRequests = approvalService.getPendingApprovals(loginMember.getId());
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("loginMember", loginMember);
        
        return "approval/pendingList";
    }

    // 결재 상세 보기
    @GetMapping("/detail/{requestId}")
    public String approvalDetail(@PathVariable Long requestId, Model model, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }

        VacationRequest vacationRequest = vacationService.getVacationRequest(requestId);
        if (vacationRequest == null) {
            return "redirect:/approval/pending";
        }

        // 현재 사용자가 결재자 중 하나인지 확인
        boolean isApprover = approvalService.isApprover(vacationRequest, loginMember.getId());
        if (!isApprover) {
            return "redirect:/approval/pending";
        }

        // 현재 결재자 ID 찾기
        String currentApproverId = vacationRequest.getApprovalSteps().stream()
                .filter(step -> step.getStatus() == ApprovalStatus.PENDING)
                .findFirst()
                .map(step -> step.getApprover().getId())
                .orElse(null);

        model.addAttribute("vacationRequest", vacationRequest);
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("approvalSteps", vacationRequest.getApprovalSteps());
        model.addAttribute("currentApproverId", currentApproverId);
        
        return "approval/approvalDetail";
    }

    // 결재 처리 (승인/반려)
    @PostMapping("/process/{requestId}")
    @ResponseBody
    public String processApproval(@PathVariable Long requestId,
                                 @RequestParam ApprovalStatus decision,
                                 @RequestParam(required = false) String comment,
                                 HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "ERROR: 로그인이 필요합니다.";
        }

        try {
            approvalService.processApproval(requestId, loginMember.getId(), decision, comment);
            return "SUCCESS";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // 내가 신청한 휴가 목록
    @GetMapping("/my-requests")
    public String myRequests(Model model, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }

        List<VacationRequest> myRequests = vacationService.getMyVacationRequests(loginMember.getId());
        model.addAttribute("myRequests", myRequests);
        model.addAttribute("loginMember", loginMember);
        
        return "approval/myRequests";
    }

    // 내가 신청한 휴가 상세 보기
    @GetMapping("/my-detail/{requestId}")
    public String myRequestDetail(@PathVariable Long requestId, Model model, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }

        VacationRequest vacationRequest = vacationService.getVacationRequest(requestId);
        if (vacationRequest == null) {
            return "redirect:/approval/my-requests";
        }

        // 현재 사용자가 신청자인지 확인
        if (!vacationRequest.getApplicant().getId().equals(loginMember.getId())) {
            return "redirect:/approval/my-requests";
        }

        model.addAttribute("vacationRequest", vacationRequest);
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("approvalSteps", vacationRequest.getApprovalSteps());
        
        return "approval/myRequestDetail";
    }
}
