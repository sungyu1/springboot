package jpabook.jpashop.exception;

public class NoSuchMemberException extends RuntimeException{
    public  NoSuchMemberException(){
        super(("존재하지 않는 아이디 입니다."));
    }
}
