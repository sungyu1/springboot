package jpabook.jpashop.controller;

import jpabook.jpashop.domain.VacationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VacationRequestForm {
    private String substituteId; //대직자 ID
    private String deptLeaderId; //부서장 ID

    private VacationType vacationType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String signatureImage; //싸인이미지
}
