// 설정 관리

// 계정 탈퇴 모달 표시
function showDeleteAccountModal() {
    document.getElementById('deleteAccountModal').style.display = 'flex';
}

// 계정 탈퇴 모달 닫기
function closeDeleteAccountModal() {
    document.getElementById('deleteAccountModal').style.display = 'none';
}

// 계정 탈퇴
async function deleteAccount() {
    try {
        const response = await fetch('/web/user', {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error('계정 탈퇴에 실패했습니다');
        }

        // 탈퇴 성공
        showToast('✓ 계정이 탈퇴되었습니다');

        // 모달 닫기
        closeDeleteAccountModal();

        // 1초 후 로그아웃 및 홈으로 이동
        setTimeout(() => {
            window.location.href = '/logout';
        }, 1000);

    } catch (error) {
        console.error('Error:', error);
        showToast('✗ ' + error.message);
    }
}

// 모달 외부 클릭시 닫기
window.addEventListener('click', function(event) {
    const modal = document.getElementById('deleteAccountModal');
    if (event.target === modal) {
        closeDeleteAccountModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeDeleteAccountModal();
    }
});
