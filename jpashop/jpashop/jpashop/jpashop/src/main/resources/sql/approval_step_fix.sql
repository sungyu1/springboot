-- Oracle approval_step 테이블 컬럼명 수정 스크립트
-- step_order 컬럼명을 step_order_num으로 변경 (Oracle 예약어 충돌 방지)

-- 기존 테이블이 있다면 삭제 (주의: 데이터 손실)
-- DROP TABLE approval_step CASCADE CONSTRAINTS;

-- approval_step 테이블 생성 (수정된 컬럼명 사용)
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

-- 시퀀스가 없다면 생성
CREATE SEQUENCE approval_step_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 권한 부여
GRANT SELECT ON approval_step_seq TO SHCHIS;
GRANT SELECT, INSERT, UPDATE, DELETE ON approval_step TO SHCHIS;

-- 인덱스 생성 (성능 향상)
CREATE INDEX idx_approval_step_vacation_request ON approval_step(vacation_request_id);
CREATE INDEX idx_approval_step_approver ON approval_step(approver_id);
CREATE INDEX idx_approval_step_status ON approval_step(status);
