document.addEventListener("DOMContentLoaded", () => {
    const logoutSection = document.getElementById("logoutSection");

    if (!logoutSection) return;

    // 초기 버튼 생성
    const originalBtn = document.createElement("button");
    originalBtn.id = "logoutBtn";
    originalBtn.className = "btn btn-outline-danger";
    originalBtn.textContent = "로그아웃";
    originalBtn.onclick = showConfirm;

    function showConfirm() {
        logoutSection.innerHTML = `
            <p style="margin-bottom: 0.5rem;">로그아웃 하시겠습니까?</p>
            <button id="confirmYes" class="btn btn-danger me-2">예</button>
            <button id="confirmNo" class="btn btn-secondary">아니오</button>
        `;
        // ✅ 예 클릭 시 홈으로 이동
        document.getElementById("confirmYes").onclick = () => {
            window.location.href = "/";
        };
        // ✅ 아니오 클릭 시 기존 버튼 복원
        document.getElementById("confirmNo").onclick = () => {
            logoutSection.innerHTML = "";
            logoutSection.appendChild(originalBtn);
        };
    }

    // 보일 때 다시 버튼 삽입
    logoutSection.appendChild(originalBtn);
});
