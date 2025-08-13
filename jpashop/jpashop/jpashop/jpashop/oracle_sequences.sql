-- Oracle 시퀀스 생성 스크립트
-- 휴가 신청 시퀀스
CREATE SEQUENCE vacation_request_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 결재 단계 시퀀스
CREATE SEQUENCE approval_step_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 시퀀스 권한 부여 (필요한 경우)
GRANT SELECT ON vacation_request_seq TO SHCHIS;
GRANT SELECT ON approval_step_seq TO SHCHIS;

