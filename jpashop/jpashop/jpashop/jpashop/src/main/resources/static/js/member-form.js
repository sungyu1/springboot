// ==== 1. 서명 관련 캔버스 기능 ====
const canvas = document.getElementById('signature-pad');
const ctx = canvas.getContext('2d');
let drawing = false;

function getPosition(event) {
    const rect = canvas.getBoundingClientRect();
    return {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top
    };
}

canvas.addEventListener('mousedown', (e) => {
    drawing = true;
    const pos = getPosition(e);
    ctx.beginPath();
    ctx.moveTo(pos.x, pos.y);
});

canvas.addEventListener('mouseup', () => {
    drawing = false;
    ctx.closePath();
});

canvas.addEventListener('mouseout', () => {
    drawing = false;
    ctx.closePath();
});

canvas.addEventListener('mousemove', (e) => {
    if (!drawing) return;
    const pos = getPosition(e);
    ctx.lineWidth = 2;
    ctx.lineCap = 'round';
    ctx.strokeStyle = '#000';
    ctx.lineTo(pos.x, pos.y);
    ctx.stroke();
});

function clearSignature() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
}

function isCanvasBlank(canvas) {
    const context = canvas.getContext('2d');
    const pixelData = context.getImageData(0, 0, canvas.width, canvas.height).data;
    return !Array.from(pixelData).some(channel => channel !== 0);
}

// ==== 2. 제출 시 서명 검증 ====
document.querySelector('form').addEventListener('submit', function (e) {
    if (isCanvasBlank(canvas)) {
        e.preventDefault();
        document.getElementById('signature-error').style.display = 'block';
        return;
    }
    const dataURL = canvas.toDataURL('image/png');
    document.getElementById('signatureData').value = dataURL;
});

// ==== 3. 아이디 중복 체크 기능 ====
function checkDuplicateId() {
    const idInput = document.getElementById('userIdInput');
    const id = idInput.value.trim();
    const resultMsg = document.getElementById('id-check-result');

    if (id === "") {
        resultMsg.innerText = "아이디를 입력하세요.";
        resultMsg.style.color = "red";
        return;
    }

    fetch(`/members/check-id?id=${encodeURIComponent(id)}`)
        .then(res => res.text())
        .then(data => {
            if (data === "AVAILABLE") {
                resultMsg.innerText = "사용 가능한 아이디입니다.";
                resultMsg.style.color = "green";
            } else {
                resultMsg.innerText = "이미 사용 중인 아이디입니다.";
                resultMsg.style.color = "red";
            }
        })
        .catch(() => {
            resultMsg.innerText = "중복 확인 중 오류가 발생했습니다.";
            resultMsg.style.color = "red";
        });
}

// ==== 4. 버튼에 이벤트 연결 ====
document.getElementById('check-id-btn').addEventListener('click', checkDuplicateId);
