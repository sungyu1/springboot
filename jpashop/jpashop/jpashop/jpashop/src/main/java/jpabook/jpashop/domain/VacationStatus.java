package jpabook.jpashop.domain;

public enum VacationStatus {
    DRAFT, PENDING, APPROVED, REJECTED, CANCELED;
    
    @Override
    public String toString() {
        return this.name();
    }
}
