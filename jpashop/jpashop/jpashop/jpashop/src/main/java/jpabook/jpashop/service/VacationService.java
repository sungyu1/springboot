package jpabook.jpashop.service;

import jakarta.transaction.Transactional;
import jpabook.jpashop.controller.VacationController;
import jpabook.jpashop.controller.VacationRequestForm;
import jpabook.jpashop.domain.*;
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
        return vacationRepository.findByApplicantIdOrderBySubmittedAtDesc(applicantId);
    }
}
