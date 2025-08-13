-- 기존 approval_step 테이블 컬럼명 변경 스크립트
-- step_order 컬럼을 step_order_num으로 변경

-- 1. 새 컬럼 추가
ALTER TABLE approval_step ADD step_order_num NUMBER;

-- 2. 기존 데이터 복사
UPDATE approval_step SET step_order_num = step_order;

-- 3. 새 컬럼을 NOT NULL로 설정
ALTER TABLE approval_step MODIFY step_order_num NOT NULL;

-- 4. 기존 컬럼 삭제
ALTER TABLE approval_step DROP COLUMN step_order;

-- 5. comment 컬럼을 approval_comment로 변경
ALTER TABLE approval_step ADD approval_comment CLOB;
UPDATE approval_step SET approval_comment = comment;
ALTER TABLE approval_step DROP COLUMN comment;

-- 6. 새 컬럼에 인덱스 생성 (필요한 경우)
CREATE INDEX idx_approval_step_order_num ON approval_step(step_order_num);

-- 변경사항 확인
SELECT column_name, data_type, nullable 
FROM user_tab_columns 
WHERE table_name = 'APPROVAL_STEP' 
ORDER BY column_id;
