package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Table(name="member")
@Entity
@Getter
@Setter
public class Member {

    @Id
    @Column(name = "member_id") // Oracle의 userid
    private String id; // Oracle userid와 매핑

    private String name;

    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Embedded
    private Address address;

    private String deptCode;    // Oracle deptcode
    private Integer jobType;    // Oracle jobtype
    private Integer useFlag;    // Oracle useflag

    @Lob
    @Column(columnDefinition = "CLOB")
    private String signatureImage;

}
