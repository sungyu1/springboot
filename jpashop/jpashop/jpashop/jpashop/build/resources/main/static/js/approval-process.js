// 결재 처리 JavaScript

function processApproval(decision) {
    const comment = document.getElementById('comment').value;
    const requestId = document.getElementById('requestId').value;
    
    // 확인 메시지
    const confirmMessage = decision === 'APPROVED' ? 
        '이 휴가 신청을 승인하시겠습니까?' : 
        '이 휴가 신청을 반려하시겠습니까?';
    
    if (confirm(confirmMessage)) {
        // 로딩 표시
        showLoading();
        
        // 결재 처리 요청
        fetch(`/approval/process/${requestId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `decision=${decision}&comment=${encodeURIComponent(comment)}`
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(result => {
            hideLoading();
            
            if (result === 'SUCCESS') {
                const successMessage = decision === 'APPROVED' ? 
                    '승인되었습니다.' : 
                    '반려되었습니다.';
                
                alert(successMessage);
                window.location.href = '/approval/pending';
            } else {
                alert('처리 중 오류가 발생했습니다: ' + result);
            }
        })
        .catch(error => {
            hideLoading();
            console.error('Error:', error);
            alert('처리 중 오류가 발생했습니다. 다시 시도해주세요.');
        });
    }
}

// 로딩 표시
function showLoading() {
    const buttons = document.querySelectorAll('.button-group button');
    buttons.forEach(button => {
        button.disabled = true;
        button.textContent = '처리 중...';
    });
}

// 로딩 숨기기
function hideLoading() {
    const buttons = document.querySelectorAll('.button-group button');
    buttons.forEach(button => {
        button.disabled = false;
        if (button.classList.contains('primary')) {
            button.textContent = '승인';
        } else if (!button.classList.contains('secondary')) {
            button.textContent = '반려';
        }
    });
}

// 결재 의견 글자 수 제한
function limitCommentLength() {
    const commentTextarea = document.getElementById('comment');
    const maxLength = 500;
    
    if (commentTextarea.value.length > maxLength) {
        commentTextarea.value = commentTextarea.value.substring(0, maxLength);
    }
    
    // 글자 수 표시 업데이트
    const charCount = document.getElementById('charCount');
    if (charCount) {
        charCount.textContent = `${commentTextarea.value.length}/${maxLength}`;
    }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    const commentTextarea = document.getElementById('comment');
    if (commentTextarea) {
        commentTextarea.addEventListener('input', limitCommentLength);
        limitCommentLength(); // 초기 글자 수 표시
    }
});

