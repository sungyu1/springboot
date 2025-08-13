package jpabook.jpashop.domain;

public enum ApprovalStatus {
    PENDING, APPROVED, REJECTED;
    
    @Override
    public String toString() {
        return this.name();
    }
}
