package jpabook.jpashop.service;

import jpabook.jpashop.domain.ApprovalStatus;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.VacationRequest;
import jpabook.jpashop.domain.VacationStatus;
import jpabook.jpashop.repository.ApprovalStepRepository;
import jpabook.jpashop.repository.VacationRepository;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalService {

    private final ApprovalStepRepository approvalStepRepository;
    private final VacationRepository vacationRepository;
    private final MemberRepository memberRepository;

    // 특정 결재자의 대기 중인 휴가 신청 목록 조회
    public List<VacationRequest> getPendingApprovals(String approverId) {
        List<Object[]> results = approvalStepRepository.findPendingApprovalsByApproverIdNative(approverId);
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
        request.setStatus(jpabook.jpashop.domain.VacationStatus.valueOf((String) row[8]));
        
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

    // 현재 사용자가 해당 휴가 신청의 결재자인지 확인
    public boolean isApprover(VacationRequest vacationRequest, String userId) {
        return vacationRequest.getApprovalSteps().stream()
                .anyMatch(step -> step.getApprover().getId().equals(userId));
    }

    // 결재 처리 (승인/반려)
    public void processApproval(Long requestId, String approverId, ApprovalStatus decision, String comment) {
        VacationRequest vacationRequest = vacationRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("휴가 신청을 찾을 수 없습니다."));

        // 현재 결재 단계 찾기
        var currentStep = vacationRequest.getApprovalSteps().stream()
                .filter(step -> step.getStatus() == ApprovalStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 결재 단계가 없습니다."));

        // 결재자가 맞는지 확인
        if (!currentStep.getApprover().getId().equals(approverId)) {
            throw new IllegalArgumentException("해당 결재를 처리할 권한이 없습니다.");
        }

        // 결재 처리
        currentStep.setStatus(decision);
        currentStep.setComment(comment);
        currentStep.setApprovedAt(LocalDateTime.now());

        // 반려인 경우 휴가 신청 상태를 반려로 변경
        if (decision == ApprovalStatus.REJECTED) {
            vacationRequest.setStatus(VacationStatus.REJECTED);
        } else {
            // 승인인 경우 다음 단계 확인
            boolean hasNextPendingStep = vacationRequest.getApprovalSteps().stream()
                    .anyMatch(step -> step.getStatus() == ApprovalStatus.PENDING);

            if (!hasNextPendingStep) {
                // 모든 단계가 승인됨 - 최종 승인
                vacationRequest.setStatus(VacationStatus.APPROVED);
                vacationRequest.setFinalApprovedAt(LocalDateTime.now());
            }
        }

        // 변경사항 저장
        vacationRepository.save(vacationRequest);
    }

    // 특정 휴가 신청의 현재 결재 단계 조회
    public int getCurrentApprovalStep(VacationRequest vacationRequest) {
        return vacationRequest.getApprovalSteps().stream()
                .filter(step -> step.getStatus() == ApprovalStatus.PENDING)
                .findFirst()
                .map(step -> step.getStepOrder())
                .orElse(0);
    }
}

