package jpabook.jpashop.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {

    @NotEmpty(message = "아이디 이름는 필수 입니다.")
    private String id;

    @NotEmpty(message = "회원 이름은 필수입니다.")
    private String name;

    private String email;

    @NotEmpty(message = "패스워드를 입력해야 합니다.")
    private String password;

    private String deptCode;    // 부서 코드
    private Integer jobType;    // 직무유형
    private Integer useFlag;    // 근무상태 (1=재직, 0=퇴사)



    private String phone;

    private String city;
    private String zipcode;

    private String signatureData;//싸인 이미지 저장


}
