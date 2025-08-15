-- Oracle 데이터베이스 완전 스키마 생성 스크립트
-- 모든 테이블을 새로 생성 (기존 데이터 삭제됨)

-- 1. 기존 테이블 삭제 (주의: 모든 데이터 손실)
DROP TABLE approval_step CASCADE CONSTRAINTS;
DROP TABLE vacation_request CASCADE CONSTRAINTS;
DROP TABLE member CASCADE CONSTRAINTS;

-- 2. 시퀀스 삭제
DROP SEQUENCE vacation_request_seq;
DROP SEQUENCE approval_step_seq;

-- 3. member 테이블 생성
CREATE TABLE member (
    member_id VARCHAR2(50) PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
    email VARCHAR2(100),
    password VARCHAR2(255) NOT NULL,
    phone VARCHAR2(20),
    address VARCHAR2(500),
    dept_code VARCHAR2(10),
    job_type NUMBER,
    use_flag NUMBER DEFAULT 1,
    signature_image CLOB
);

-- 4. vacation_request 테이블 생성
CREATE TABLE vacation_request (
    vacation_request_id NUMBER PRIMARY KEY,
    applicant_id VARCHAR2(50) NOT NULL,
    substitute_id VARCHAR2(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days NUMBER NOT NULL,
    vacation_type VARCHAR2(50) NOT NULL,
    reason VARCHAR2(1000),
    status VARCHAR2(20) NOT NULL,
    submitted_at TIMESTAMP,
    final_approved_at TIMESTAMP,
    signature_image CLOB,
    CONSTRAINT fk_vacation_request_applicant 
        FOREIGN KEY (applicant_id) REFERENCES member(member_id),
    CONSTRAINT fk_vacation_request_substitute 
        FOREIGN KEY (substitute_id) REFERENCES member(member_id)
);

-- 5. approval_step 테이블 생성
CREATE TABLE approval_step (
    approval_step_id NUMBER PRIMARY KEY,
    vacation_request_id NUMBER NOT NULL,
    approver_id VARCHAR2(50) NOT NULL,
    step_order_num NUMBER NOT NULL,
    status VARCHAR2(20) NOT NULL,
    approval_comment CLOB,
    approved_at TIMESTAMP,
    CONSTRAINT fk_approval_step_vacation_request 
        FOREIGN KEY (vacation_request_id) REFERENCES vacation_request(vacation_request_id),
    CONSTRAINT fk_approval_step_approver 
        FOREIGN KEY (approver_id) REFERENCES member(member_id)
);

-- 6. 시퀀스 생성
CREATE SEQUENCE vacation_request_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE SEQUENCE approval_step_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 7. 권한 부여
GRANT SELECT ON vacation_request_seq TO SHCHIS;
GRANT SELECT ON approval_step_seq TO SHCHIS;
GRANT SELECT, INSERT, UPDATE, DELETE ON member TO SHCHIS;
GRANT SELECT, INSERT, UPDATE, DELETE ON vacation_request TO SHCHIS;
GRANT SELECT, INSERT, UPDATE, DELETE ON approval_step TO SHCHIS;

-- 8. 인덱스 생성
CREATE INDEX idx_member_dept_code ON member(dept_code);
CREATE INDEX idx_member_job_type ON member(job_type);
CREATE INDEX idx_vacation_request_applicant ON vacation_request(applicant_id);
CREATE INDEX idx_vacation_request_status ON vacation_request(status);
CREATE INDEX idx_approval_step_vacation_request ON approval_step(vacation_request_id);
CREATE INDEX idx_approval_step_approver ON approval_step(approver_id);
CREATE INDEX idx_approval_step_status ON approval_step(status);
CREATE INDEX idx_approval_step_order_num ON approval_step(step_order_num);

-- 9. 테이블 생성 확인
SELECT table_name FROM user_tables WHERE table_name IN ('MEMBER', 'VACATION_REQUEST', 'APPROVAL_STEP');

-- 10. 컬럼 정보 확인
SELECT table_name, column_name, data_type, nullable 
FROM user_tab_columns 
WHERE table_name IN ('MEMBER', 'VACATION_REQUEST', 'APPROVAL_STEP')
ORDER BY table_name, column_id;


