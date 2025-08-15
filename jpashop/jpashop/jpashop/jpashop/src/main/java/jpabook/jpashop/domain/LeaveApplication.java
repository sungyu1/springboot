package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "leave_application")
public class LeaveApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Member applicant; // 신청자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substitute_id")
    private Member substitute; // 대체근무자

    @Column(name = "leave_type")
    private String leaveType;

    @Lob
    @Column(name = "leave_detail", columnDefinition = "CLOB")
    private String leaveDetail;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_days")
    private Integer totalDays;

    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "current_approval_step")
    private String currentApprovalStep;

    @Lob
    @Column(name = "approval_history", columnDefinition = "CLOB")
    private String approvalHistory;

    @Lob
    @Column(name = "form_data_json", columnDefinition = "CLOB")
    private String formDataJson;

    @Lob
    @Column(name = "rejection_reason", columnDefinition = "CLOB")
    private String rejectionReason;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "is_printable")
    private String isPrintable;

    @Column(name = "is_substitute_approved")
    private String isSubstituteApproved;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_applicant_signed")
    private String isApplicantSigned;

    @Column(name = "is_dept_head_approved")
    private String isDeptHeadApproved;

    @Column(name = "is_hr_staff_approved")
    private String isHrStaffApproved;

    @Column(name = "is_center_director_approved")
    private String isCenterDirectorApproved;

    @Column(name = "is_admin_director_approved")
    private String isAdminDirectorApproved;

    @Column(name = "is_ceo_director_approved")
    private String isCeoDirectorApproved;

    @Column(name = "current_approver_id")
    private String currentApproverId;

    @Column(name = "is_final_approved")
    private String isFinalApproved;

    @Column(name = "final_approver_id")
    private String finalApproverId;

    @Column(name = "final_approval_date")
    private LocalDateTime finalApprovalDate;

    @Column(name = "final_approval_step")
    private String finalApprovalStep;
}
