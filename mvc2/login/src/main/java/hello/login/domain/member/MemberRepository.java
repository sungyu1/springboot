package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class MemberRepository {

    private static Map<Long, Member> store = new HashMap<>(); //static 사용
    private static long sequence=0L; //static 사용

   public Member save(Member member) {
       member.setId(++sequence);
       log.info("save: member={}", member);
        store.put(member.getId(), member);
        return member;
   }

    // 회원 찾는것
    public Member findById(Long id){
       return store.get(id);
    }


    // 전체 회원 찾는것
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    // 로그인 아이디 찾는 방법
    public Optional<Member> findByLoginId(String loginId) {
        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();
    }

    public void clearStore() {
        store.clear();
    }
}
