package jpabook.jpashop.domain;

public enum VacationType {
    연차휴가("연차휴가"),
    경조휴가("경조휴가"),
    특별휴가("특별휴가"),
    생리휴가("생리휴가"),
    공민휴가("공민휴가"),
    유급산전휴가("유급/산전휴가"),
    병가("병가"),
    기타("기타");

    private final String displayName;

    VacationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
