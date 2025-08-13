package jpabook.jpashop.service;

import jakarta.transaction.Transactional;
import jpabook.jpashop.controller.VacationController;
import jpabook.jpashop.controller.VacationRequestForm;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.ApprovalStepRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;
    private final MemberRepository memberRepository;
    private final ApprovalStepRepository approvalStepRepository;
    @Transactional
    public void submitVacation(Member applicant, Member substitute, Member hrStaff, VacationRequestForm form) {
        VacationRequest request = new VacationRequest();
        request.setApplicant(applicant);
        request.setSubstitute(substitute);
        request.setStartDate(form.getStartDate());
        request.setEndDate(form.getEndDate());
        request.setTotalDays(form.getDays());
        request.setReason(form.getReason());
        request.setStatus(VacationStatus.PENDING);
        request.setVacationType(form.getVacationType());
        request.setSubmittedAt(LocalDateTime.now());

        // 회원가입 시 등록한 서명을 그대로 저장
        request.setSignatureImage(applicant.getSignatureImage());

        // 부서장 자동 찾기
        Member deptLeader = findDepartmentLeader(applicant);
        if (deptLeader == null) {
            throw new IllegalStateException("해당 부서의 부서장을 찾을 수 없습니다.");
        }

        List<ApprovalStep> steps = List.of(
                createStep(request, substitute, 1),
                createStep(request, deptLeader, 2),
                createStep(request, hrStaff, 3)
        );
        request.setApprovalSteps(steps);

        vacationRepository.save(request);
    }

    private ApprovalStep createStep(VacationRequest request, Member approver, int order) {
        ApprovalStep step = new ApprovalStep();
        step.setVacationRequest(request);
        step.setApprover(approver);
        step.setStepOrder(order);
        step.setStatus(ApprovalStatus.PENDING);
        return step;
    }

    private Member findDepartmentLeader(Member applicant) {
        // 신청자의 부서 코드가 null인 경우 처리
        if (applicant.getDeptCode() == null) {
            throw new IllegalStateException("신청자의 부서 정보가 없습니다.");
        }
        
        return memberRepository.findAll().stream()
                .filter(m -> m.getDeptCode() != null && m.getDeptCode().equals(applicant.getDeptCode()))
                .filter(m -> m.getJobType() == 2) // 2: 부서장
                .findFirst()
                .orElse(null);
    }

    private Member findCenterHead() {
        return memberRepository.findAll().stream()
                .filter(m -> m.getJobType() == 5) // 5: 센터장
                .findFirst()
                .orElse(null);
    }

    // 휴가 신청 조회
    public VacationRequest getVacationRequest(Long requestId) {
        return vacationRepository.findById(requestId).orElse(null);
    }

    // 내가 신청한 휴가 목록 조회
    public List<VacationRequest> getMyVacationRequests(String applicantId) {
        List<Object[]> results = vacationRepository.findMyVacationRequestsNative(applicantId);
        List<VacationRequest> requests = results.stream()
                .map(this::mapToVacationRequest)
                .toList();
        
        // 각 휴가 신청에 대해 ApprovalSteps 정보 추가
        for (VacationRequest request : requests) {
            List<ApprovalStep> steps = findApprovalStepsByRequestId(request.getId());
            request.setApprovalSteps(steps);
        }
        
        return requests;
    }

    // 휴가 신청 ID로 결재 단계들 조회
    private List<ApprovalStep> findApprovalStepsByRequestId(Long requestId) {
        List<Object[]> results = approvalStepRepository.findApprovalStepsByRequestIdNative(requestId);
        return results.stream()
                .map(this::mapToApprovalStep)
                .toList();
    }

    // Object[]를 ApprovalStep으로 변환
    private ApprovalStep mapToApprovalStep(Object[] row) {
        ApprovalStep step = new ApprovalStep();
        
        // Oracle NUMBER 타입은 BigDecimal로 반환되므로 적절히 변환
        step.setId(((java.math.BigDecimal) row[0]).longValue());
        
        // vacation_request_id는 사용하지 않음 (이미 VacationRequest에 연결됨)
        // step.setVacationRequest(vacationRequest);
        
        // approver_id로 Member 조회
        String approverId = (String) row[2];
        Member approver = findMemberById(approverId);
        step.setApprover(approver);
        
        // step_order_num
        Object stepOrderObj = row[3];
        if (stepOrderObj instanceof java.math.BigDecimal) {
            step.setStepOrder(((java.math.BigDecimal) stepOrderObj).intValue());
        } else {
            step.setStepOrder((Integer) stepOrderObj);
        }
        
        // status
        step.setStatus(jpabook.jpashop.domain.ApprovalStatus.valueOf((String) row[4]));
        
        // approval_comment
        step.setComment((String) row[5]);
        
        // approved_at
        Object approvedAtObj = row[6];
        if (approvedAtObj != null) {
            if (approvedAtObj instanceof java.sql.Timestamp) {
                step.setApprovedAt(((java.sql.Timestamp) approvedAtObj).toLocalDateTime());
            } else {
                step.setApprovedAt((java.time.LocalDateTime) approvedAtObj);
            }
        }
        
        return step;
    }

    // Object[]를 VacationRequest로 변환 (ApprovalService와 동일한 로직)
    private VacationRequest mapToVacationRequest(Object[] row) {
        VacationRequest request = new VacationRequest();
        
        // Oracle NUMBER 타입은 BigDecimal로 반환되므로 적절히 변환
        request.setId(((java.math.BigDecimal) row[0]).longValue());
        
        // Member 정보 조회
        String applicantId = (String) row[1];
        String substituteId = (String) row[2];
        
        Member applicant = findMemberById(applicantId);
        Member substitute = findMemberById(substituteId);
        
        request.setApplicant(applicant);
        request.setSubstitute(substitute);
        
        // Oracle DATE 타입은 Timestamp로 반환되므로 LocalDate로 변환
        request.setStartDate(((java.sql.Timestamp) row[3]).toLocalDateTime().toLocalDate());
        request.setEndDate(((java.sql.Timestamp) row[4]).toLocalDateTime().toLocalDate());
        
        // total_days도 BigDecimal일 수 있음
        Object totalDaysObj = row[5];
        if (totalDaysObj instanceof java.math.BigDecimal) {
            request.setTotalDays(((java.math.BigDecimal) totalDaysObj).intValue());
        } else {
            request.setTotalDays((Integer) totalDaysObj);
        }
        
        request.setVacationType(jpabook.jpashop.domain.VacationType.valueOf((String) row[6]));
        request.setReason((String) row[7]);
        
        // status 설정 - 디버깅 로그 추가
        String statusStr = (String) row[8];
        System.out.println("DEBUG: VacationRequest ID=" + request.getId() + ", Status from DB=" + statusStr);
        System.out.println("DEBUG: Row[8] type=" + (row[8] != null ? row[8].getClass().getName() : "null"));
        System.out.println("DEBUG: Row[8] value=" + row[8]);
        
        if (statusStr != null) {
            request.setStatus(jpabook.jpashop.domain.VacationStatus.valueOf(statusStr));
            System.out.println("DEBUG: VacationRequest ID=" + request.getId() + ", Status after setting=" + request.getStatus());
        } else {
            System.out.println("DEBUG: Status is null for VacationRequest ID=" + request.getId());
        }
        
        // submitted_at과 final_approved_at도 Timestamp일 수 있음
        Object submittedAtObj = row[9];
        if (submittedAtObj instanceof java.sql.Timestamp) {
            request.setSubmittedAt(((java.sql.Timestamp) submittedAtObj).toLocalDateTime());
        } else {
            request.setSubmittedAt((java.time.LocalDateTime) submittedAtObj);
        }
        
        Object finalApprovedAtObj = row[10];
        if (finalApprovedAtObj != null) {
            if (finalApprovedAtObj instanceof java.sql.Timestamp) {
                request.setFinalApprovedAt(((java.sql.Timestamp) finalApprovedAtObj).toLocalDateTime());
            } else {
                request.setFinalApprovedAt((java.time.LocalDateTime) finalApprovedAtObj);
            }
        }
        
        return request;
    }

    // Member ID로 Member 찾기
    private Member findMemberById(String memberId) {
        List<Member> members = memberRepository.findById(memberId);
        return members.isEmpty() ? createEmptyMember(memberId) : members.get(0);
    }

    // 빈 Member 객체 생성
    private Member createEmptyMember(String memberId) {
        Member member = new Member();
        member.setId(memberId);
        member.setName("알 수 없음");
        return member;
    }
}
