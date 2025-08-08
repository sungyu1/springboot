package hello.login.domain.member;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class Member {
// 데이터 베이스에 저장되는 아이디 (자동생성)
    private Long id;

// 사용자가 만들때 들어가는 아이디
    @NotEmpty(message = "아이디를 입력해주세요.")
    private String loginId; //로그인 ID
// 사용자 만들때 들어가는 이름
    @NotEmpty(message = "이름을 입력해주세요.")
    private String name; //사용자 이름
//  사용자가 만들때 들어가는 비밀번호
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    private String password; //비밀 번호
//  사용자의 주민번호
    @NotEmpty(message = "주민번호를 입력해주세요")
    @Pattern(regexp = "\\d{6}-\\d{7}", message = "주민번호 형식이 올바르지 않습니다. 예: 901231-1234567" )
    private String ssn;//주민 번호
//  사용자의 주소
    @NotEmpty(message = "주소를 입력해주세요.")
    private String address;//주소
//  사용자 전화번호
    @NotEmpty(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "01[0-1,6-9]-\\d{3,4}-\\d{4}", message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
    private String phone;
//  사용자 부서명
    @NotEmpty(message = "부서명을 입력해주세요.")
    private String department;//부서명
}
