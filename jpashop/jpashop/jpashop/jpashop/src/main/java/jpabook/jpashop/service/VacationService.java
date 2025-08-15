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
    public void submitVacation(Member applicant, Member substitute, Member deptLeader, VacationRequestForm form) {
        VacationRequest request = new VacationRequest();
        request.setApplicant(applicant);
        request.setSubstitute(substitute);
        request.setStartDate(form.getStartDate());
        request.setEndDate(form.getEndDate());
        request.setReason(form.getReason());
        request.setStatus(VacationStatus.PENDING);
        request.setVacationType(form.getVacationType());
        request.setSubmittedAt(LocalDateTime.now());

        // 회원가입 시 등록한 서명을 그대로 저장 (BLOB 타입이므로 주석 처리)
        // request.setSignatureImage(applicant.getSignatureImage());

        List<ApprovalStep> steps = List.of(
                createStep(request, substitute, 1),
                createStep(request, deptLeader, 2)
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
        return memberRepository.findAll().stream()
                .filter(m -> m.getDeptCode().equals(applicant.getDeptCode()))
                .filter(m -> "2".equals(m.getJobLevel())) // 2: 부서장
                .findFirst()
                .orElse(null);
    }

    private Member findCenterHead() {
        return memberRepository.findAll().stream()
                .filter(m -> "5".equals(m.getJobLevel())) // 5: 행정원장
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
        return results.stream()
                .map(this::mapToVacationRequest)
                .toList();
    }

    // Object[]를 VacationRequest로 변환
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
        
        // status 설정
        String statusStr = (String) row[8];
        if (statusStr != null) {
            request.setStatus(jpabook.jpashop.domain.VacationStatus.valueOf(statusStr));
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
