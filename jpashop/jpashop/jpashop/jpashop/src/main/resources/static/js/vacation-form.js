document.addEventListener("DOMContentLoaded", function () {
    const startDateInput = document.querySelector("input[name='startDate']");
    const endDateInput = document.querySelector("input[name='endDate']");
    const daysInput = document.querySelector("input[name='days']");

    function calculateDays() {
        const start = new Date(startDateInput.value);
        const end = new Date(endDateInput.value);
        if (start && end && !isNaN(start) && !isNaN(end)) {
            const timeDiff = end - start;
            const days = Math.floor(timeDiff / (1000 * 60 * 60 * 24)) + 1; // 시작일 포함
            if (days > 0) {
                daysInput.value = days;
            } else {
                daysInput.value = "";
            }
        }
    }

    startDateInput.addEventListener("change", calculateDays);
    endDateInput.addEventListener("change", calculateDays);
});
// 휴가원 서명 입력
function showSignature() {
    const signatureData = /*[[${loginMember.signatureImage}]]*/ '';
    if (signatureData) {
        const img = document.getElementById('signaturePreview');
        img.src = signatureData;
        img.style.display = 'block';
        document.getElementById('signatureImage').value = signatureData;
    } else {
        alert('등록된 서명이 없습니다. 회원정보에서 서명을 등록해주세요.');
    }
}