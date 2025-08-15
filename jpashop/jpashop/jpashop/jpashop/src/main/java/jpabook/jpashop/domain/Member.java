package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Table(name="usrmst_inf")
@Entity
@Getter
@Setter
public class Member {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "passwd", length = 100)
    private String password;

    @Column(name = "deptcode")
    private String deptCode;

    @Column(name = "joblevel")
    private String jobLevel;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "useflag")
    private String useFlag;

    @Column(name = "signpath")
    private String signPath;

    @Lob
    @Column(name = "signimage", columnDefinition = "BLOB")
    private byte[] signatureImage;  //서명이미지 저장 (BLOB)

    
    @Column(name = "user_role")
    private String userRole;

    // 기본값 설정을 위한 메서드들
    public void setDefaultValues() {
        if (this.useFlag == null) {
            this.useFlag = "1"; // 기본값: 근무중
        }

        if (this.userRole == null) {
            this.userRole = "USER"; // 기본값: 일반 사용자
        }
        if (this.signPath == null) {
            this.signPath = "C:\\Users\\user\\Desktop\\signs"; // 기본값: 서명 저장 경로
        }
    }
}
