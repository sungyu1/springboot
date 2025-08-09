package jpabook.jpashop.service;

import jpabook.jpashop.controller.VacationRequestForm;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.oracle.OracleUser;
import jpabook.jpashop.exception.BadCredentialsException;
import jpabook.jpashop.exception.NoSuchMemberException;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.oracle.OracleUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final OracleUserRepository oracleUserRepository; // 🔴 이 라인 추가 필요

    /**
     * 회원가입
     **/
    @Transactional(readOnly = false) //쓰기에는 readOnly= false
    public String join(Member member) {
        validateDuplicateMember(member); // 중복회원 있는지 검증
        memberRepository.save(member);
        return member.getId();
    }

    // 서버(Oracle)에 있는 사용자만 회원가입 허용
    private void validateCanRegisterFromOracle(String id) {
        OracleUser oracleUser = oracleUserRepository.findByUserId(id);
        if (oracleUser == null || oracleUser.getUseFlag() != 1) {
            throw new IllegalStateException("서버에 등록된 사용자만 회원가입할 수 있습니다.");
        }
    }

    // 로컬 DB 중복 확인
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findById(member.getId());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 단건 조회
    public Member findOne(String memberId) {
        return memberRepository.findOne(memberId);
    }

    // MemberService.java
    public boolean isIdAvailable(String id) {
        List<Member> members = memberRepository.findById(id);
        return members.isEmpty(); // true면 사용 가능, false면 중복
    }

    /**
     * 로그인 (로컬 DB 기준)
     */
    @Transactional(readOnly = true)
    public Member login(String id, String password) {
        Member member = memberRepository.findOne(id);
        if (member == null) {
            throw new NoSuchMemberException();
        }
        // 해시가 아니라면 현행 그대로 비교
        if (!member.getPassword().equals(password)) {
            throw new BadCredentialsException();
        }
        return member;
    }


    // 추후 Oracle 연동 시 활성화할 수 있는 메서드
    @Transactional(readOnly = false)
    public Member syncFromOracle(String id, String rawPassword) {
        OracleUser oracleUser = oracleUserRepository.findByUserId(id);
        if (oracleUser != null && oracleUser.getUseFlag() == 1) {
            Member newMember = new Member();
            newMember.setId(oracleUser.getUserId());
            newMember.setName(oracleUser.getName());
            newMember.setPassword(rawPassword); // TODO: 암호화 적용 가능성
            newMember.setAddress(new Address("oracleCity", "oracleStreet"));
            newMember.setSignatureImage(null);
            return memberRepository.save(newMember);
        }
        throw new IllegalArgumentException("Oracle 서버에 해당 유저 정보가 없습니다.");
    }

    public List<Member> findByDeptAndJob(String deptCode, int jobType) {
        return memberRepository.findAll().stream()
                .filter(m -> deptCode.equals(m.getDeptCode()))
                .filter(m -> m.getJobType() != null && m.getJobType() == jobType)
                .toList();
    }

    private ApprovalStep createStep(VacationRequest req, Member approver, int order) {
        ApprovalStep step = new ApprovalStep();
        step.setVacationRequest(req);
        step.setApprover(approver);
        step.setStepOrder(order);
        step.setStatus(ApprovalStatus.PENDING);
        return step;
    }
}
