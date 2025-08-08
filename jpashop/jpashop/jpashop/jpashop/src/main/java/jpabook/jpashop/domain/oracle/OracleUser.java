package jpabook.jpashop.domain.oracle;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_mst_test")
public class OracleUser {

    @Id
    @Column(name = "userid")
    private String userId;

    @Column(name = "name")
    private String name;

    @Column(name = "deptcode")
    private String deptCode;

    @Column(name = "jobtype")
    private Integer jobType;

    @Column(name = "useflag")
    private Integer useFlag;
}


