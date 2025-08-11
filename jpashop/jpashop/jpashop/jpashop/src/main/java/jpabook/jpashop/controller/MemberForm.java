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

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "패스워드는 필수입니다.")
    @Size(min = 4, max = 64, message = "패스워드는 4~64자로 입력하세요.")
    private String password;

    @NotBlank(message = "부서 코드는 필수입니다.")
    private String deptCode;    // 부서 코드

    @NotNull(message = "직무유형은 필수입니다.")
    private Integer jobType;    // 직무유형

    @NotNull(message = "근무상태는 필수입니다.")
    @Min(value = 0, message = "근무상태는 0 또는 1이어야 합니다.")
    @Max(value = 1, message = "근무상태는 0 또는 1이어야 합니다.")
    private Integer useFlag;    // 근무상태 (1=재직, 0=퇴사)

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @NotBlank(message = "도시는 필수입니다.")
    private String city;

    @NotEmpty(message = "도로명은 필수입니다.")
    private String zipcode;

    private String signatureData;//싸인 이미지 저장


}
