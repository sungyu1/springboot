package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    /**
     * 회원 저장
     */
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    /**
     * 회원 단건 조회 (기본키 기준)
     */
    public Member findOne(String id) {
        return em.find(Member.class, id);
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    /**
     * 로그인: ID + 패스워드 일치하는 회원 찾기
     */
    public Optional<Member> findByIdAndPassword(String id, String password) {
        if (id == null || password == null) return Optional.empty();

        return em.createQuery("select m from Member m where m.id = :id and m.password = :password", Member.class)
                .setParameter("id", id)
                .setParameter("password", password)
                .getResultStream()
                .findFirst();
    }

    /**
     * 회원가입 시 아이디 중복 확인
     */
    public List<Member> findById(String id) {
        return em.createQuery("select m from Member m where m.id = :id", Member.class)
                .setParameter("id", id)
                .getResultList();
    }
}
