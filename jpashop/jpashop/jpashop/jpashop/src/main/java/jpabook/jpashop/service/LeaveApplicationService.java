package jpabook.jpashop.service;

import jpabook.jpashop.controller.VacationRequestForm;
import jpabook.jpashop.domain.LeaveApplication;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.LeaveApplicationRepository;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void submitLeaveApplication(Member applicant, Member substitute, VacationRequestForm form) {
        LeaveApplication application = new LeaveApplication();
        
        application.setApplicant(applicant);
        application.setSubstitute(substitute);
        application.setStartDate(form.getStartDate());
        application.setEndDate(form.getEndDate());
        application.setTotalDays(form.getDays());
        application.setLeaveDetail(form.getReason());
        application.setLeaveType(form.getVacationType().name());
        application.setStatus("PENDING");
        application.setApplicationDate(LocalDate.now());
        application.setCurrentApprovalStep("SUBSTITUTE");
        application.setCurrentApproverId(substitute.getId());
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        
        // 초기 승인 상태 설정
        application.setIsApplicantSigned("Y");
        application.setIsSubstituteApproved("N");
        application.setIsDeptHeadApproved("N");
        application.setIsHrStaffApproved("N");
        application.setIsCenterDirectorApproved("N");
        application.setIsAdminDirectorApproved("N");
        application.setIsCeoDirectorApproved("N");
        application.setIsFinalApproved("N");
        application.setIsPrintable("N");

        leaveApplicationRepository.save(application);
    }

    // 휴가 신청 조회
    public LeaveApplication getLeaveApplication(Long applicationId) {
        return leaveApplicationRepository.findById(applicationId).orElse(null);
    }

    // 내가 신청한 휴가 목록 조회
    public List<LeaveApplication> getMyLeaveApplications(String applicantId) {
        return leaveApplicationRepository.findByApplicantId(applicantId);
    }

    // 결재자별 대기 중인 휴가 신청 목록 조회
    public List<LeaveApplication> getPendingApprovals(String approverId) {
        return leaveApplicationRepository.findPendingByApproverId(approverId);
    }

    // 결재 처리
    @Transactional
    public void processApproval(Long applicationId, String approverId, boolean isApproved, String comment) {
        LeaveApplication application = leaveApplicationRepository.findById(applicationId).orElse(null);
        if (application == null) {
            throw new IllegalArgumentException("휴가 신청을 찾을 수 없습니다.");
        }

        // 현재 결재 단계에 따른 처리
        String currentStep = application.getCurrentApprovalStep();
        
        if ("SUBSTITUTE".equals(currentStep)) {
            application.setIsSubstituteApproved(isApproved ? "Y" : "N");
            if (isApproved) {
                application.setCurrentApprovalStep("DEPT_HEAD");
                // 부서장 찾기
                List<Member> deptHeads = memberRepository.findByDeptCodeAndJobLevel(
                    application.getApplicant().getDeptCode(), "2");
                if (!deptHeads.isEmpty()) {
                    application.setCurrentApproverId(deptHeads.get(0).getId());
                }
            } else {
                application.setStatus("REJECTED");
                application.setRejectionReason(comment);
                application.setIsFinalApproved("N");
            }
        } else if ("DEPT_HEAD".equals(currentStep)) {
            application.setIsDeptHeadApproved(isApproved ? "Y" : "N");
            if (isApproved) {
                application.setCurrentApprovalStep("HR_STAFF");
                // 인사담당자 찾기
                List<Member> hrStaff = memberRepository.findByJobLevel("3");
                if (!hrStaff.isEmpty()) {
                    application.setCurrentApproverId(hrStaff.get(0).getId());
                }
            } else {
                application.setStatus("REJECTED");
                application.setRejectionReason(comment);
                application.setIsFinalApproved("N");
            }
        } else if ("HR_STAFF".equals(currentStep)) {
            application.setIsHrStaffApproved(isApproved ? "Y" : "N");
            if (isApproved) {
                // 모든 결재 완료
                application.setStatus("APPROVED");
                application.setIsFinalApproved("Y");
                application.setFinalApproverId(approverId);
                application.setFinalApprovalDate(LocalDateTime.now());
                application.setFinalApprovalStep("HR_STAFF");
            } else {
                application.setStatus("REJECTED");
                application.setRejectionReason(comment);
                application.setIsFinalApproved("N");
            }
        }

        application.setUpdatedAt(LocalDateTime.now());
        leaveApplicationRepository.save(application);
    }
}
