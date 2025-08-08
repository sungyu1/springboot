package jpabook.jpashop.repository.oracle;

import jpabook.jpashop.domain.oracle.OracleUser;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OracleUserRepository extends JpaRepository<OracleUser, String> {
    OracleUser findByUserId(String userId);
}
