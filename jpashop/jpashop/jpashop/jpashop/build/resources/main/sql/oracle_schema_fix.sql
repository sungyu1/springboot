-- Oracle 데이터베이스 스키마 수정 스크립트
-- 모든 테이블의 컬럼명과 데이터 타입을 Oracle에 맞게 수정

-- 1. vacation_request 테이블 수정
-- signature_image 컬럼을 CLOB으로 변경
ALTER TABLE vacation_request MODIFY signature_image CLOB;

-- 2. member 테이블 수정
-- signature_image 컬럼을 CLOB으로 변경
ALTER TABLE member MODIFY signature_image CLOB;

-- 3. approval_step 테이블 수정
-- step_order 컬럼을 step_order_num으로 변경
ALTER TABLE approval_step ADD step_order_num NUMBER;
UPDATE approval_step SET step_order_num = step_order;
ALTER TABLE approval_step MODIFY step_order_num NOT NULL;
ALTER TABLE approval_step DROP COLUMN step_order;

-- comment 컬럼을 approval_comment로 변경 (Oracle 예약어 충돌 방지)
ALTER TABLE approval_step ADD approval_comment CLOB;
UPDATE approval_step SET approval_comment = comment;
ALTER TABLE approval_step DROP COLUMN comment;

-- 4. 시퀀스 생성 (없는 경우)
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

-- 5. 권한 부여
GRANT SELECT ON vacation_request_seq TO SHCHIS;
GRANT SELECT ON approval_step_seq TO SHCHIS;

-- 6. 인덱스 생성
CREATE INDEX idx_approval_step_order_num ON approval_step(step_order_num);
CREATE INDEX idx_approval_step_vacation_request ON approval_step(vacation_request_id);
CREATE INDEX idx_approval_step_approver ON approval_step(approver_id);
CREATE INDEX idx_approval_step_status ON approval_step(status);

-- 7. 변경사항 확인
SELECT table_name, column_name, data_type, nullable 
FROM user_tab_columns 
WHERE table_name IN ('VACATION_REQUEST', 'MEMBER', 'APPROVAL_STEP')
ORDER BY table_name, column_id;
