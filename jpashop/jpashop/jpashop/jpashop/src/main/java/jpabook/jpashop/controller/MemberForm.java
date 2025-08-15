package jpabook.jpashop.controller;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {

    @NotEmpty(message = "아이디는 필수 입니다.")
    private String id;

    @NotBlank(message = "회원 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "패스워드는 필수입니다.")
    @Size(min = 4, max = 20, message = "패스워드는 4~20자로 입력하세요.")
    private String password;

    @NotBlank(message = "부서 코드는 필수입니다.")
    private String deptCode;    // 부서 코드

    @NotBlank(message = "직급은 필수입니다.")
    private String jobLevel;    // 직급

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    private String useFlag;    // 근무상태 (자동으로 "1" 설정)

    private String signatureData; // 서명 이미지 (Base64 문자열)

    // 선택적 필드들 (기본값으로 설정될 수 있는 것들)
    private String userRole;             // 사용자 역할

}
