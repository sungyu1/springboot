package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "approval_step")
public class ApprovalStep {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approval_step_seq")
    @SequenceGenerator(name = "approval_step_seq", sequenceName = "approval_step_seq", allocationSize = 1)
    @Column(name = "approval_step_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacation_request_id")
    private VacationRequest vacationRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private Member approver;

    @Column(name = "step_order_num")
    private Integer stepOrder; // 1: 대체자, 2: 팀장, 3: 센터장

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApprovalStatus status;

    @Column(name = "approval_comment")
    private String comment;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}