package jpabook.jpashop.repository;

import jpabook.jpashop.domain.VacationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationRepository extends JpaRepository<VacationRequest, Long> {
}
