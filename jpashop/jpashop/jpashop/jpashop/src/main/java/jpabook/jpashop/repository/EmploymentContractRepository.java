package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.EmploymentContract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmploymentContractRepository {

    private final EntityManager em;

    /**
     * 고용계약서 저장
     */
    public EmploymentContract save(EmploymentContract contract) {
        em.persist(contract);
        return contract;
    }

    /**
     * 고용계약서 단건 조회
     */
    public Optional<EmploymentContract> findById(Long id) {
        return Optional.ofNullable(em.find(EmploymentContract.class, id));
    }

    /**
     * 전체 고용계약서 조회
     */
    public List<EmploymentContract> findAll() {
        return em.createQuery("select ec from EmploymentContract ec", EmploymentContract.class)
                .getResultList();
    }

    /**
     * 생성자별 고용계약서 조회
     */
    public List<EmploymentContract> findByCreatorId(String creatorId) {
        return em.createQuery("select ec from EmploymentContract ec where ec.creatorId = :creatorId order by ec.createdAt desc", EmploymentContract.class)
                .setParameter("creatorId", creatorId)
                .getResultList();
    }

    /**
     * 직원별 고용계약서 조회
     */
    public List<EmploymentContract> findByEmployeeId(String employeeId) {
        return em.createQuery("select ec from EmploymentContract ec where ec.employeeId = :employeeId order by ec.createdAt desc", EmploymentContract.class)
                .setParameter("employeeId", employeeId)
                .getResultList();
    }

    /**
     * 상태별 고용계약서 조회
     */
    public List<EmploymentContract> findByStatus(String status) {
        return em.createQuery("select ec from EmploymentContract ec where ec.status = :status order by ec.createdAt desc", EmploymentContract.class)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * 계약타입별 고용계약서 조회
     */
    public List<EmploymentContract> findByContractType(String contractType) {
        return em.createQuery("select ec from EmploymentContract ec where ec.contractType = :contractType order by ec.createdAt desc", EmploymentContract.class)
                .setParameter("contractType", contractType)
                .getResultList();
    }
}
