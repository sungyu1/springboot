package jpabook.jpashop.repository;

import jpabook.jpashop.domain.VacationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<VacationRequest, Long> {
    
    // 신청자 ID로 휴가 신청 목록 조회 (최신순)
    List<VacationRequest> findByApplicantIdOrderBySubmittedAtDesc(String applicantId);
}
