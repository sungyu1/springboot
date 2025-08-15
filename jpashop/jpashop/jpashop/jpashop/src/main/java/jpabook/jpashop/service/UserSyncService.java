package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.oracle.OracleUser;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.oracle.OracleUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final MemberRepository localUserRepository;
    private final OracleUserRepository oracleUserRepository;

    @Transactional
    public Optional<Member> syncUserIfExists(String userId) {
        Optional<Member> localUser = Optional.ofNullable(localUserRepository.findOne(userId));

        if (localUser.isPresent()) {
            return localUser;
        }

        Optional<OracleUser> oracleUser = oracleUserRepository.findById(userId);
        if (oracleUser.isPresent()) {
            OracleUser oUser = oracleUser.get();

            Member newMember = new Member();
            newMember.setId(oUser.getUserId());
            newMember.setName(oUser.getName());
            newMember.setDeptCode(oUser.getDeptCode());
            newMember.setJobLevel(oUser.getJobType().toString()); // jobType을 jobLevel로 매핑
            newMember.setUseFlag(oUser.getUseFlag().toString());
            newMember.setPassword("default123"); // 초기 비밀번호 설정

            localUserRepository.save(newMember);
            return Optional.of(newMember);
        }

        return Optional.empty();
    }
}

