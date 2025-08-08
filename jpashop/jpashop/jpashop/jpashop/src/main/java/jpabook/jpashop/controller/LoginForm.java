package jpabook.jpashop.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {

    @NotEmpty(message = "id을 입력해주세요")
    private String id;

    @NotEmpty(message = "패스워드를 입력해주세요")
    private String password;


}
