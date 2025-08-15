package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "employment_contract")
public class EmploymentContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "status", nullable = false)
    private String status;

    @Lob
    @Column(name = "form_data_json", columnDefinition = "CLOB", nullable = false)
    private String formDataJson;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "jpg_url")
    private String jpgUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "contract_type")
    private String contractType;

    @Column(name = "printable", nullable = false)
    private String printable;

    @Lob
    @Column(name = "rejection_reason", columnDefinition = "CLOB")
    private String rejectionReason;
}
