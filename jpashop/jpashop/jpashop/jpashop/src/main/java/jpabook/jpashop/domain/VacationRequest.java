package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "vacation_request")
public class VacationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vacation_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Member applicant; // 신청자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substitute_id")
    private Member substitute; // 대체근무자

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_days", nullable = false)
    private Integer totalDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "vacation_type", nullable = false)
    private VacationType vacationType;

    @Column(name = "reason")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VacationStatus status;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "final_approved_at")
    private LocalDateTime finalApprovedAt;

    @Lob
    @Column(name = "signature_image", columnDefinition = "TEXT")
    private String signatureImage;  //서명이미지 저장

    @OneToMany(mappedBy = "vacationRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprovalStep> approvalSteps;
}