document.addEventListener("DOMContentLoaded", function () {
    const startDateInput = document.querySelector("input[name='startDate']");
    const endDateInput = document.querySelector("input[name='endDate']");
    const daysInput = document.querySelector("input[name='days']");

    function calculateWorkingDays() {
        const start = new Date(startDateInput.value);
        const end = new Date(endDateInput.value);
        
        if (start && end && !isNaN(start) && !isNaN(end)) {
            // 시작일이 종료일보다 늦으면 경고
            if (start > end) {
                alert('시작일은 종료일보다 이전이어야 합니다.');
                daysInput.value = "";
                return;
            }
            
            let workingDays = 0;
            const currentDate = new Date(start);
            
            // 시작일부터 종료일까지 반복
            while (currentDate <= end) {
                const dayOfWeek = currentDate.getDay(); // 0: 일요일, 6: 토요일
                
                // 주말(토요일, 일요일)이 아닌 경우만 카운트
                if (dayOfWeek !== 0 && dayOfWeek !== 6) {
                    workingDays++;
                }
                
                // 다음 날로 이동
                currentDate.setDate(currentDate.getDate() + 1);
            }
            
            if (workingDays > 0) {
                daysInput.value = workingDays;
            } else {
                daysInput.value = "";
                alert('선택한 기간에 근무일이 없습니다. (주말만 포함된 기간)');
            }
        }
    }

    startDateInput.addEventListener("change", calculateWorkingDays);
    endDateInput.addEventListener("change", calculateWorkingDays);
});
// 휴가원 서명 입력
function showSignature() {
    // hidden input에서 서명 데이터와 사용자 이름 가져오기
    const signatureData = document.getElementById('userSignatureData').value;
    const userName = document.getElementById('userNameData').value;
    
    // 디버깅을 위한 콘솔 로그
    console.log('서명 데이터:', signatureData);
    console.log('사용자 이름:', userName);
    
    if (signatureData && signatureData.trim() !== '') {
        // (인) 자리에 서명 이미지 표시
        const signatureMark = document.getElementById('signatureMark');
        signatureMark.innerHTML = '<img src="' + signatureData + '" style="max-height: 20px; max-width: 60px; vertical-align: middle;" />';
        
        // hidden 필드에 서명 데이터 저장
        document.getElementById('signatureImage').value = signatureData;
        
        // 서명 확인 버튼 텍스트 변경
        const signatureBtn = document.querySelector('.signature-btn');
        signatureBtn.textContent = '서명 숨기기';
        signatureBtn.onclick = hideSignature;
    } else {
        alert(userName + '님의 등록된 서명이 없습니다. 회원정보에서 서명을 등록해주세요.');
    }
}

// 서명 숨기기
function hideSignature() {
    // (인) 자리에 다시 (인) 표시
    const signatureMark = document.getElementById('signatureMark');
    signatureMark.textContent = '(인)';
    
    // 서명 확인 버튼 텍스트 변경
    const signatureBtn = document.querySelector('.signature-btn');
    signatureBtn.textContent = '서명 확인';
    signatureBtn.onclick = showSignature;
}