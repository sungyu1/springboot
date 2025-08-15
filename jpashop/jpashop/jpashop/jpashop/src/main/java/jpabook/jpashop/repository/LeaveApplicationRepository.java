package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.LeaveApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LeaveApplicationRepository {

    private final EntityManager em;

    /**
     * 휴가 신청 저장
     */
    public LeaveApplication save(LeaveApplication leaveApplication) {
        em.persist(leaveApplication);
        return leaveApplication;
    }

    /**
     * 휴가 신청 단건 조회
     */
    public Optional<LeaveApplication> findById(Long id) {
        return Optional.ofNullable(em.find(LeaveApplication.class, id));
    }

    /**
     * 전체 휴가 신청 조회
     */
    public List<LeaveApplication> findAll() {
        return em.createQuery("select la from LeaveApplication la", LeaveApplication.class)
                .getResultList();
    }

    /**
     * 신청자별 휴가 신청 조회
     */
    public List<LeaveApplication> findByApplicantId(String applicantId) {
        return em.createQuery("select la from LeaveApplication la where la.applicant.id = :applicantId order by la.applicationDate desc", LeaveApplication.class)
                .setParameter("applicantId", applicantId)
                .getResultList();
    }

    /**
     * 결재자별 대기 중인 휴가 신청 조회
     */
    public List<LeaveApplication> findPendingByApproverId(String approverId) {
        return em.createQuery("select la from LeaveApplication la where la.currentApproverId = :approverId and la.status = 'PENDING' order by la.applicationDate desc", LeaveApplication.class)
                .setParameter("approverId", approverId)
                .getResultList();
    }

    /**
     * 상태별 휴가 신청 조회
     */
    public List<LeaveApplication> findByStatus(String status) {
        return em.createQuery("select la from LeaveApplication la where la.status = :status order by la.applicationDate desc", LeaveApplication.class)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * 휴가 타입별 조회
     */
    public List<LeaveApplication> findByLeaveType(String leaveType) {
        return em.createQuery("select la from LeaveApplication la where la.leaveType = :leaveType order by la.applicationDate desc", LeaveApplication.class)
                .setParameter("leaveType", leaveType)
                .getResultList();
    }
}
