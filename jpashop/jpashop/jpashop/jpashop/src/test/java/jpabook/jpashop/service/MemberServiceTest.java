package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional // test 끝나고 롤백
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    //회원가입
    @Test
    @Rollback(value = false)
    public void 회원가입() throws Exception {
        //given
        Member member=new Member();
        member.setName("김선규");
        member.setEmail("hong@test.com");
        member.setPassword("1234");
        // when
        String saveId= memberService.join(member);
        // then
        em.flush();
       assertEquals(member, memberRepository.findOne(saveId));
    }


//    중복회원 예외
    @Test
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1=new Member();
        member1.setName("김선규");
        member1.setEmail("hong@test.com");
        member1.setPassword("1234");

        Member member2=new Member();
        member2.setName("김선규");
        member2.setEmail("hong@test.com");
        member2.setPassword("1234");
        // when
        memberService.join(member1);
        try{
            memberService.join(member2);// 예외가 발생 해야 한다!!
        }catch (IllegalStateException e){
            return;
        }
        // then
        fail("예외 발생해야 한다.");
    }

    //로그인
    @Test
    public void 로그인_성공() throws Exception {
        //given
        Member member = new Member();
        member.setName("김햄");
        member.setEmail("hong@test.com");
        member.setPassword("1234");
        memberService.join(member);
        // when
        Member loginMember = memberService.login("hong@test.com", "1234");
        // then
        assertNotNull(loginMember);
        assertEquals("김햄",loginMember.getName());
    }
}