package jpabook.jpashop.controller;

import jakarta.servlet.http.HttpSession;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.LeaveApplication;
import jpabook.jpashop.service.LeaveApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/approval")
public class ApprovalController {

    private final LeaveApplicationService leaveApplicationService;

    // 결재 대기 목록
    @GetMapping("/pending")
    public String pendingApprovals(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/members/login";
        }

        List<LeaveApplication> pendingRequests = leaveApplicationService.getPendingApprovals(loginMember.getId());
        model.addAttribute("requests", pendingRequests);
        return "approval/pendingList";
    }

    // 결재 상세 보기
    @GetMapping("/detail/{requestId}")
    public String approvalDetail(@PathVariable Long requestId, HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/members/login";
        }

        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplication(requestId);
        if (leaveApplication == null) {
            return "redirect:/approval/pending";
        }

        model.addAttribute("request", leaveApplication);
        model.addAttribute("currentApproverId", leaveApplication.getCurrentApproverId());
        return "approval/approvalDetail";
    }

    // 결재 처리
    @PostMapping("/process/{requestId}")
    public String processApproval(@PathVariable Long requestId,
                                 @RequestParam String action,
                                 @RequestParam(required = false) String comment,
                                 HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/members/login";
        }

        boolean isApproved = "approve".equals(action);
        leaveApplicationService.processApproval(requestId, loginMember.getId(), isApproved, comment);

        return "redirect:/approval/pending";
    }

    // 내 신청 목록
    @GetMapping("/my-requests")
    public String myRequests(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/members/login";
        }

        List<LeaveApplication> myRequests = leaveApplicationService.getMyLeaveApplications(loginMember.getId());
        model.addAttribute("requests", myRequests);
        return "approval/myRequests";
    }

    // 내 신청 상세 보기
    @GetMapping("/my-detail/{requestId}")
    public String myRequestDetail(@PathVariable Long requestId, HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/members/login";
        }

        LeaveApplication leaveApplication = leaveApplicationService.getLeaveApplication(requestId);
        if (leaveApplication == null || !leaveApplication.getApplicant().getId().equals(loginMember.getId())) {
            return "redirect:/approval/my-requests";
        }

        model.addAttribute("request", leaveApplication);
        return "approval/myRequestDetail";
    }
}
