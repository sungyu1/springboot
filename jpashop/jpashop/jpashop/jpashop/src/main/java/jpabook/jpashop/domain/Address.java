package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable //jpa 내장타입
@Getter
public class Address {

    private String address; // 전체 주소를 한 번에 저장

    protected Address(){

    }

    public Address(String address){
        this.address = address;
    }

}
