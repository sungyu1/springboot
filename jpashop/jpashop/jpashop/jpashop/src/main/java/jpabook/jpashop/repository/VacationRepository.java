package jpabook.jpashop.repository;

import jpabook.jpashop.domain.VacationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<VacationRequest, Long> {
    
    // 신청자 ID로 휴가 신청 목록 조회 (최신순) - Native Query 사용
    @Query(value = "SELECT vr.vacation_request_id, vr.applicant_id, vr.substitute_id, " +
                   "vr.start_date, vr.end_date, vr.total_days, vr.vacation_type, vr.reason, " +
                   "vr.status, vr.submitted_at, vr.final_approved_at " +
                   "FROM vacation_request vr " +
                   "WHERE vr.applicant_id = :applicantId " +
                   "ORDER BY vr.submitted_at DESC", nativeQuery = true)
    List<Object[]> findMyVacationRequestsNative(@Param("applicantId") String applicantId);
}
