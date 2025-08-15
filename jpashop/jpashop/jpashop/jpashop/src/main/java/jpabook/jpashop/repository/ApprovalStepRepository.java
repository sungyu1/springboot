package jpabook.jpashop.repository;

import jpabook.jpashop.domain.ApprovalStep;
import jpabook.jpashop.domain.VacationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {

    // 특정 결재자의 대기 중인 휴가 신청 목록 조회 (Native Query - CLOB 제외)
    @Query(value = "SELECT DISTINCT vr.vacation_request_id, vr.applicant_id, vr.substitute_id, " +
                   "vr.start_date, vr.end_date, vr.total_days, vr.vacation_type, vr.reason, " +
                   "vr.status, vr.submitted_at, vr.final_approved_at " +
                   "FROM vacation_request vr " +
                   "JOIN approval_step aps ON vr.vacation_request_id = aps.vacation_request_id " +
                   "WHERE aps.approver_id = :approverId " +
                   "AND aps.status = 'PENDING' " +
                   "ORDER BY vr.submitted_at DESC", nativeQuery = true)
    List<Object[]> findPendingApprovalsByApproverIdNative(@Param("approverId") String approverId);

    // 휴가 신청 ID로 결재 단계들을 순서대로 조회 (Native Query - CLOB 제외)
    @Query(value = "SELECT aps.approval_step_id, aps.vacation_request_id, aps.approver_id, " +
                   "aps.step_order_num, aps.status, aps.approval_comment, aps.approved_at " +
                   "FROM approval_step aps " +
                   "WHERE aps.vacation_request_id = :requestId " +
                   "ORDER BY aps.step_order_num", nativeQuery = true)
    List<Object[]> findApprovalStepsByRequestIdNative(@Param("requestId") Long requestId);

    // 휴가 신청 ID로 결재 단계들을 순서대로 조회
    List<ApprovalStep> findByVacationRequestIdOrderByStepOrder(Long vacationRequestId);

    // 특정 휴가 신청의 특정 단계 결재자 조회
    @Query("SELECT aps FROM ApprovalStep aps " +
           "WHERE aps.vacationRequest.id = :requestId " +
           "AND aps.stepOrder = :stepOrder")
    ApprovalStep findByVacationRequestIdAndStepOrder(@Param("requestId") Long requestId, 
                                                    @Param("stepOrder") Integer stepOrder);

    // 결재 단계 업데이트 (Native Query)
    @Query(value = "UPDATE approval_step SET status = :status, approval_comment = :comment, approved_at = :approvedAt " +
                   "WHERE approval_step_id = :stepId", nativeQuery = true)
    void updateApprovalStep(@Param("stepId") Long stepId, 
                           @Param("status") String status, 
                           @Param("comment") String comment, 
                           @Param("approvedAt") java.time.LocalDateTime approvedAt);
}

