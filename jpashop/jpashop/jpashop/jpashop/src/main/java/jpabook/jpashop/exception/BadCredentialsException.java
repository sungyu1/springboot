package jpabook.jpashop.exception;

public class BadCredentialsException extends RuntimeException{
    public BadCredentialsException(){
        super("비밀번호가 일치하지 않습니다.");
    }
}
