package jpabook.jpashop.service;

import jpabook.jpashop.domain.ApprovalStatus;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.VacationRequest;
import jpabook.jpashop.domain.VacationStatus;
import jpabook.jpashop.repository.ApprovalStepRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
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

    // 결재자가 해당 휴가 신청의 결재자인지 확인
    public boolean isApprover(Long requestId, String approverId) {
        return approvalStepRepository.findPendingApprovalsByApproverIdNative(approverId)
                .stream()
                .anyMatch(row -> ((java.math.BigDecimal) row[1]).longValue() == requestId);
    }

    // 결재 처리
    @Transactional
    public void processApproval(Long requestId, String approverId, boolean isApproved, String comment) {
        VacationRequest request = vacationRepository.findById(requestId).orElse(null);
        if (request == null) {
            throw new IllegalArgumentException("휴가 신청을 찾을 수 없습니다.");
        }

        // 현재 결재 단계 찾기
        List<Object[]> currentSteps = approvalStepRepository.findApprovalStepsByRequestIdNative(requestId);
        Object[] currentStepData = currentSteps.stream()
                .filter(row -> approverId.equals(row[2]) && "PENDING".equals(row[4]))
                .findFirst()
                .orElse(null);

        if (currentStepData == null) {
            throw new IllegalArgumentException("결재할 수 있는 단계가 없습니다.");
        }

        // 결재 단계 업데이트
        Long stepId = ((java.math.BigDecimal) currentStepData[0]).longValue();
        approvalStepRepository.updateApprovalStep(stepId, 
                isApproved ? ApprovalStatus.APPROVED.name() : ApprovalStatus.REJECTED.name(), 
                comment, 
                LocalDateTime.now());

        // 다음 결재 단계 확인
        List<Object[]> remainingSteps = currentSteps.stream()
                .filter(row -> "PENDING".equals(row[4]))
                .toList();

        if (remainingSteps.size() <= 1) { // 현재 단계만 남았거나 모든 단계 완료
            if (isApproved) {
                // 모든 결재 완료
                request.setStatus(VacationStatus.APPROVED);
                request.setFinalApprovedAt(LocalDateTime.now());
            } else {
                // 반려됨
                request.setStatus(VacationStatus.REJECTED);
            }
            vacationRepository.save(request);
        }
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
